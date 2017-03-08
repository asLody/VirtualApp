package com.lody.virtual.server.pm;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Pair;

import com.lody.virtual.client.core.InstallStrategy;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.Constants;
import com.lody.virtual.helper.compat.NativeLibraryHelperCompat;
import com.lody.virtual.helper.compat.PackageParserCompat;
import com.lody.virtual.remote.AppSetting;
import com.lody.virtual.remote.InstallResult;
import com.lody.virtual.helper.utils.FileUtils;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.os.VEnvironment;
import com.lody.virtual.os.VUserHandle;
import com.lody.virtual.server.IAppManager;
import com.lody.virtual.server.accounts.VAccountManagerService;
import com.lody.virtual.server.am.BroadcastSystem;
import com.lody.virtual.server.am.UidSystem;
import com.lody.virtual.server.am.VActivityManagerService;
import com.lody.virtual.server.interfaces.IAppObserver;
import com.lody.virtual.server.interfaces.IAppRequestListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Lody
 */
public class VAppManagerService extends IAppManager.Stub {

    private static final String TAG = VAppManagerService.class.getSimpleName();
    private static final AtomicReference<VAppManagerService> gService = new AtomicReference<>();
    private final UidSystem mUidSystem = new UidSystem();
    private boolean isBooting;
    private RemoteCallbackList<IAppObserver> mRemoteCallbackList = new RemoteCallbackList<IAppObserver>();

    private IAppRequestListener listener;

    public static VAppManagerService get() {
        return gService.get();
    }

    public static void systemReady() {
        VEnvironment.systemReady();
        VAppManagerService instance = new VAppManagerService();
        instance.mUidSystem.initUidList();
        gService.set(instance);
    }

    public boolean isBooting() {
        return isBooting;
    }

    public void preloadAllApps() {
        isBooting = true;
        for (File appDir : VEnvironment.getDataAppDirectory().listFiles()) {
            String pkgName = appDir.getName();
            if ("android".equals(pkgName)) {
                continue;
            }
            File storeFile = new File(appDir, "base.apk");
            int flags = 0;
            if (!storeFile.exists()) {
                ApplicationInfo appInfo = null;
                try {
                    appInfo = VirtualCore.get().getUnHookPackageManager()
                            .getApplicationInfo(pkgName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    // Ignore
                }
                if ((appInfo == null || appInfo.publicSourceDir == null)) {
                    FileUtils.deleteDir(appDir);
                    for (int userId : VUserManagerService.get().getUserIds()) {
                        FileUtils.deleteDir(VEnvironment.getDataUserPackageDirectory(userId, pkgName));
                    }
                    continue;
                }
                storeFile = new File(appInfo.publicSourceDir);
                flags |= InstallStrategy.DEPEND_SYSTEM_IF_EXIST;
            }
            InstallResult res = install(storeFile.getPath(), flags, true);
            if (!res.isSuccess) {
                VLog.e(TAG, "Unable to install app %s: %s.", pkgName, res.error);
                FileUtils.deleteDir(appDir);
            }
        }
        isBooting = false;
    }

    @Override
    public InstallResult installApp(String apkPath, int flags) {
        return install(apkPath, flags, false);
    }


    private synchronized InstallResult install(String apkPath, int flags, boolean onlyScan) {
        if (apkPath == null) {
            return InstallResult.makeFailure("apk path = NULL");
        }
        File apk = new File(apkPath);
        if (!apk.exists() || !apk.isFile()) {
            return InstallResult.makeFailure("APK File is not exist.");
        }
        PackageParser.Package pkg = null;
        PackageParser parser = null;
        try {
            Pair<PackageParser, PackageParser.Package> parseResult = PackageParserCompat.parsePackage(apk, 0);
            if (parseResult != null) {
                parser = parseResult.first;
                pkg = parseResult.second;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (parser == null || pkg == null || pkg.packageName == null) {
            return InstallResult.makeFailure("Unable to parse the package.");
        }
        InstallResult res = new InstallResult();
        res.packageName = pkg.packageName;
        // PackageCache holds all packages, try to check if we need to update.
        PackageParser.Package existOne = PackageCache.get(pkg.packageName);
        AppSetting existSetting = findAppInfo(pkg.packageName);
        if (existOne != null) {
            if ((flags & InstallStrategy.IGNORE_NEW_VERSION) != 0) {
                res.isUpdate = true;
                return res;
            }
            if (!canUpdate(existOne, pkg, flags)) {
                return InstallResult.makeFailure("Not allowed to update the package.");
            }
            res.isUpdate = true;
        }
        File appDir = VEnvironment.getDataAppPackageDirectory(pkg.packageName);
        File libDir = new File(appDir, "lib");
        if (res.isUpdate) {
            FileUtils.deleteDir(libDir);
            VEnvironment.getOdexFile(pkg.packageName).delete();
            VActivityManagerService.get().killAppByPkg(pkg.packageName, VUserHandle.USER_ALL);
        }
        if (!libDir.exists() && !libDir.mkdirs()) {
            return InstallResult.makeFailure("Unable to create lib dir.");
        }
        boolean dependSystem = (flags & InstallStrategy.DEPEND_SYSTEM_IF_EXIST) != 0
                && VirtualCore.get().isOutsideInstalled(pkg.packageName);

        if (existSetting != null && existSetting.dependSystem) {
            dependSystem = false;
        }

        if (!onlyScan) {
            NativeLibraryHelperCompat.copyNativeBinaries(new File(apkPath), libDir);
            if (!dependSystem) {
                File storeFile = new File(appDir, "base.apk");
                File parentFolder = storeFile.getParentFile();
                if (!parentFolder.exists() && !parentFolder.mkdirs()) {
                    VLog.w(TAG, "Warning: unable to create folder : " + storeFile.getPath());
                } else if (storeFile.exists() && !storeFile.delete()) {
                    VLog.w(TAG, "Warning: unable to delete file : " + storeFile.getPath());
                }
                FileUtils.copyFile(apk, storeFile);
                apk = storeFile;
            }
        }
        if (existOne != null) {
            PackageCache.remove(pkg.packageName);
        }
        if (!dependSystem) {
            try {
                linkApkResForNotification(pkg.packageName, apk);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AppSetting appSetting = new AppSetting();
        appSetting.parser = parser;
        appSetting.dependSystem = dependSystem;
        appSetting.apkPath = apk.getPath();
        appSetting.libPath = libDir.getPath();
        appSetting.packageName = pkg.packageName;
        appSetting.appId = VUserHandle.getAppId(mUidSystem.getOrCreateUid(pkg));

        PackageCache.put(pkg, appSetting);
        BroadcastSystem.get().startApp(pkg);
        if (!onlyScan) {
            notifyAppInstalled(appSetting);
        }
        res.isSuccess = true;
        return res;
    }

    private void linkApkResForNotification(String packageName, File apkFile) throws Exception {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (FileUtils.isSymlink(apkFile)) {
                return;
            }
            // chmod
            // /data/data/io.virtualapp/virtual
            // /data/data/io.virtualapp/virtual/data
            // /data/data/io.virtualapp/virtual/data/app
            // /data/data/io.virtualapp/virtual/data/app/com.example.notifications/base.apk
            FileUtils.chmod(apkFile.getParentFile().getAbsolutePath(), FileUtils.FileMode.MODE_755);
            FileUtils.chmod(apkFile.getAbsolutePath(), FileUtils.FileMode.MODE_755);
        }
    }

    private boolean canUpdate(PackageParser.Package existOne, PackageParser.Package newOne, int flags) {
        if ((flags & InstallStrategy.COMPARE_VERSION) != 0) {
            if (existOne.mVersionCode < newOne.mVersionCode) {
                return true;
            }
        }
        if ((flags & InstallStrategy.TERMINATE_IF_EXIST) != 0) {
            return false;
        }
        if ((flags & InstallStrategy.UPDATE_IF_EXIST) != 0) {
            return true;
        }
        return false;
    }


    public boolean uninstallApp(String pkg) {
        synchronized (PackageCache.sPackageCaches) {
            AppSetting setting = findAppInfo(pkg);
            if (setting != null) {
                try {
                    BroadcastSystem.get().stopApp(pkg);
                    VActivityManagerService.get().killAppByPkg(pkg, VUserHandle.USER_ALL);
                    VEnvironment.getPackageResourcePath(pkg).delete();
                    FileUtils.deleteDir(VEnvironment.getDataAppPackageDirectory(pkg));
                    VEnvironment.getOdexFile(pkg).delete();
                    for (int userId : VUserManagerService.get().getUserIds()) {
                        FileUtils.deleteDir(VEnvironment.getDataUserPackageDirectory(userId, pkg));
                    }
                    PackageCache.remove(pkg);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    notifyAppUninstalled(setting);
                }
                return true;
            }
        }
        return false;
    }

    public List<AppSetting> getAllApps() {
        List<AppSetting> settings = new ArrayList<>(getAppCount());
        for (PackageParser.Package p : PackageCache.sPackageCaches.values()) {
            settings.add((AppSetting) p.mExtras);
        }
        return settings;
    }

    public int getAppCount() {
        return PackageCache.sPackageCaches.size();
    }

    public boolean isAppInstalled(String pkg) {
        return pkg != null && !"android".equals(pkg) && PackageCache.sPackageCaches.get(pkg) != null;
    }

    private void notifyAppInstalled(AppSetting setting) {
        int N = mRemoteCallbackList.beginBroadcast();
        while (N-- > 0) {
            try {
                mRemoteCallbackList.getBroadcastItem(N).onNewApp(setting.packageName);
            } catch (RemoteException e) {
                // Ignore
            }
        }
        mRemoteCallbackList.finishBroadcast();
        Intent virtualIntent = new Intent(Constants.ACTION_PACKAGE_ADDED);
        Uri uri = Uri.fromParts("package", setting.packageName, null);
        virtualIntent.setData(uri);
        for (int userId : VUserManagerService.get().getUserIds()) {
            Intent intent = new Intent(virtualIntent);
            intent.putExtra(Intent.EXTRA_UID, VUserHandle.getUid(userId, setting.appId));
            VirtualCore.get().getContext().sendBroadcast(virtualIntent);
        }
        VAccountManagerService.get().refreshAuthenticatorCache(null);
    }

    private void notifyAppUninstalled(AppSetting setting) {
        int N = mRemoteCallbackList.beginBroadcast();
        while (N-- > 0) {
            try {
                mRemoteCallbackList.getBroadcastItem(N).onRemoveApp(setting.packageName);
            } catch (RemoteException e) {
                // Ignore
            }
        }
        mRemoteCallbackList.finishBroadcast();
        Intent virtualIntent = new Intent(Constants.ACTION_PACKAGE_REMOVED);
        Uri uri = Uri.fromParts("package", setting.packageName, null);
        virtualIntent.setData(uri);
        for (int userId : VUserManagerService.get().getUserIds()) {
            Intent intent = new Intent(virtualIntent);
            intent.putExtra(Intent.EXTRA_UID, VUserHandle.getUid(userId, setting.appId));
            VirtualCore.get().getContext().sendBroadcast(virtualIntent);
        }
        VAccountManagerService.get().refreshAuthenticatorCache(null);
    }

    @Override
    public void registerObserver(IAppObserver observer) {
        try {
            mRemoteCallbackList.register(observer);
        } catch (Throwable e) {
            // Ignore
        }
    }

    @Override
    public void unregisterObserver(IAppObserver observer) {
        try {
            mRemoteCallbackList.unregister(observer);
        } catch (Throwable e) {
            // Ignore
        }
    }

    @Override
    public IAppRequestListener getAppRequestListener() {
        return listener;
    }

    @Override
    public void setAppRequestListener(final IAppRequestListener listener) {
        this.listener = listener;
        if (listener != null) {
            try {
                listener.asBinder().linkToDeath(new DeathRecipient() {
                    @Override
                    public void binderDied() {
                        listener.asBinder().unlinkToDeath(this, 0);
                        VAppManagerService.this.listener = null;
                    }
                }, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void clearAppRequestListener() {
        this.listener = null;
    }

    public AppSetting findAppInfo(String pkg) {
        synchronized (PackageCache.class) {
            if (pkg != null) {
                PackageParser.Package p = PackageCache.get(pkg);
                if (p != null) {
                    return (AppSetting) p.mExtras;
                }
            }
            return null;
        }
    }

    public int getAppId(String pkg) {
        AppSetting setting = findAppInfo(pkg);
        return setting != null ? setting.appId : -1;
    }
}
