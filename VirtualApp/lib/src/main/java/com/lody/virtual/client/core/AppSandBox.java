package com.lody.virtual.client.core;

import android.app.Application;
import android.app.LoadedApk;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.Build;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.renderscript.RenderScript;
import android.renderscript.RenderScriptCacheDir;
import android.text.TextUtils;
import android.view.HardwareRenderer;

import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.env.RuntimeEnv;
import com.lody.virtual.client.fixer.ContextFixer;
import com.lody.virtual.client.local.LocalPackageManager;
import com.lody.virtual.client.local.LocalProcessManager;
import com.lody.virtual.helper.compat.ActivityThreadCompat;
import com.lody.virtual.helper.compat.VMRuntimeCompat;
import com.lody.virtual.helper.loaders.ClassLoaderHelper;
import com.lody.virtual.helper.proto.AppInfo;
import com.lody.virtual.helper.utils.Reflect;
import com.lody.virtual.helper.utils.VLog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author Lody
 *
 */
public class AppSandBox {

	private static final String TAG = AppSandBox.class.getSimpleName();
	private static HashSet<String> installedApps = new HashSet<String>();
	private static Map<String, Application> applicationMap = new HashMap<>();

	private static String LAST_PKG;

	private static boolean sInstalling = false;

	public static Application getApplication(String pkg) {
		return applicationMap.get(pkg);
	}

	public static String getLastPkg() {
		return LAST_PKG;
	}

	public static void install(final String procName, final String pkg) {
		sInstalling = true;
		if (Looper.myLooper() == Looper.getMainLooper()) {
			installLocked(procName, pkg);
		} else {
			final CountDownLatch lock = new CountDownLatch(1);
			RuntimeEnv.getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					installLocked(procName, pkg);
					lock.countDown();
				}
			});
			try {
				lock.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		sInstalling = false;
		VLog.d(TAG, "Application of Process(%s) have launched. ", RuntimeEnv.getCurrentProcessName());
	}

	public static boolean isInstalling() {
		return sInstalling;
	}

	private static void installLocked(String procName, String pkg) {
		if (installedApps.contains(pkg)) {
			return;
		}
		LAST_PKG = pkg;
		PatchManager.fixAllSettings();
		VLog.d(TAG, "Installing %s.", pkg);
		LocalProcessManager.onAppProcessCreate(VClientImpl.getClient().asBinder());
		AppInfo appInfo = VirtualCore.getCore().findApp(pkg);
		if (appInfo == null) {
			return;
		}
//		ADD: Virtual SD Card 
//		IOHook.redirect("/sdcard/", "/sdcard/1/");
//		IOHook.redirect("/storage/emulated/0/", "/storage/emulated/0/1/");
//		IOHook.hook();
		ApplicationInfo applicationInfo = appInfo.applicationInfo;
		RuntimeEnv.setCurrentProcessName(procName, appInfo);

		LoadedApk loadedApk = createLoadedApk(appInfo);
		setupRuntime(applicationInfo);

		List<ProviderInfo> providers = null;

		try {
			PackageInfo pkgInfo = VirtualCore.getPM().getPackageInfo(pkg, PackageManager.GET_PROVIDERS);
			if (pkgInfo.providers != null) {
				providers = new ArrayList<>(pkgInfo.providers.length);
				for (ProviderInfo providerInfo : pkgInfo.providers) {
					if (providerInfo.multiprocess || TextUtils.equals(procName, providerInfo.processName)) {
						providers.add(providerInfo);
					}
				}
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		ClassLoader classLoader = loadedApk.getClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		Application app = loadedApk.makeApplication(false, null);
		Reflect.on(VirtualCore.mainThread()).set("mInitialApplication", app);
		ContextFixer.fixContext(app.getBaseContext());
		if (providers != null) {
			try {
				ActivityThreadCompat.installContentProviders(app, providers);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		VirtualCore.mainThread().getInstrumentation().callApplicationOnCreate(app);
		LocalPackageManager pm = LocalPackageManager.getInstance();
		
		List<ActivityInfo> receivers = pm.getReceivers(pkg, 0);
		for (ActivityInfo receiverInfo : receivers) {
			if (TextUtils.equals(receiverInfo.processName, procName)) {
				List<IntentFilter> filters = pm.getReceiverIntentFilter(receiverInfo);
				if (filters != null && filters.size() > 0) {
					for (IntentFilter filter : filters) {
						try {
							BroadcastReceiver receiver = (BroadcastReceiver) classLoader.loadClass(receiverInfo.name)
									.newInstance();
							if (receiverInfo.permission != null) {
								app.registerReceiver(receiver, filter, receiverInfo.permission, null);
							} else {
								app.registerReceiver(receiver, filter);
							}
						} catch (Throwable e) {
							// Ignore
						}
					}
				} else {
					try {
						BroadcastReceiver receiver = (BroadcastReceiver) classLoader.loadClass(receiverInfo.name)
								.newInstance();
						IntentFilter filter = new IntentFilter();
						filter.addAction(VirtualCore.getReceiverAction(receiverInfo.packageName, receiverInfo.name));
						if (receiverInfo.permission != null) {
							app.registerReceiver(receiver, filter, receiverInfo.permission, null);
						} else {
							app.registerReceiver(receiver, filter);
						}
					} catch (Throwable e) {
						// Ignore
					}
				}
			}
		}
		LocalProcessManager.onEnterApp(pkg);
		applicationMap.put(appInfo.packageName, app);
		installedApps.add(appInfo.packageName);
	}

	private static void setupRuntime(ApplicationInfo applicationInfo) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L
				&& applicationInfo.targetSdkVersion < Build.VERSION_CODES.L) {
			try {
				Message.updateCheckRecycle(applicationInfo.targetSdkVersion);
			} catch (Throwable e) {
				// Ignore
			}
		}
		VMRuntimeCompat.setTargetSdkVersion(applicationInfo.targetSdkVersion);

		Context appContext = createAppContext(applicationInfo);
		File codeCacheDir;

		if (Build.VERSION.SDK_INT >= 23) {
			codeCacheDir = appContext.getCodeCacheDir();
		} else {
			codeCacheDir = appContext.getCacheDir();
		}
		if (codeCacheDir != null) {
			System.setProperty("java.io.tmpdir", codeCacheDir.getPath());
			try {
				HardwareRenderer.setupDiskCache(codeCacheDir);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			if (Build.VERSION.SDK_INT >= 23) {
				try {
					RenderScriptCacheDir.setupDiskCache(codeCacheDir);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			} else if (Build.VERSION.SDK_INT >= 16) {
				try {
					Reflect.on(RenderScript.class).call("setupDiskCache", codeCacheDir);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		if (applicationInfo.targetSdkVersion <= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.ThreadPolicy.Builder builder = new StrictMode.ThreadPolicy.Builder(StrictMode.getThreadPolicy());
			builder.permitNetwork();
			StrictMode.setThreadPolicy(builder.build());
		}
	}


	private static Context createAppContext(ApplicationInfo appInfo) {
		Context context = VirtualCore.getCore().getContext();
		try {
			return context.createPackageContext(appInfo.packageName,
					Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
		} catch (PackageManager.NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static LoadedApk createLoadedApk(AppInfo appInfo) {
		ApplicationInfo applicationInfo = appInfo.applicationInfo;
		LoadedApk loadedApk = ActivityThreadCompat.getPackageInfoNoCheck(applicationInfo);
		ClassLoader classLoader = ClassLoaderHelper.create(appInfo);
		Reflect.on(loadedApk).set("mClassLoader", classLoader);
		try {
			//Fuck HUA-WEI phone
			Reflect.on(loadedApk).set("mSecurityViolation", false);
		} catch (Throwable e) {
			// Ignore
		}
		return loadedApk;
	}

	public static Set<String> getInstalledPackages() {
		return new HashSet<>(installedApps);
	}

}
