package com.lody.virtual.client.hook.patchs.pm;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Process;

import com.lody.virtual.client.env.BlackList;
import com.lody.virtual.client.hook.base.Hook;
import com.lody.virtual.client.local.LocalPackageManager;
import com.lody.virtual.helper.proto.AppInfo;
import com.lody.virtual.helper.utils.XLog;

import java.lang.reflect.Method;

/**
 * @author Lody
 *
 *
 *         适配插件的包信息获取.
 *
 *         原型: public PackageInfo getPackageInfo(String packageName, int flags,
 *         int userId)
 */
public final class Hook_GetPackageInfo extends Hook<PackageManagerPatch> {

	/**
	 * 这个构造器必须有,用于依赖注入.
	 *
	 * @param patchObject
	 *            注入对象
	 */
	public Hook_GetPackageInfo(PackageManagerPatch patchObject) {
		super(patchObject);
	}

	@Override
	public String getName() {
		return "getPackageInfo";
	}

	@Override
	public Object onHook(Object who, Method method, Object... args) throws Throwable {
		String pkg = (String) args[0];
		int flags = (int) args[1];
		if (getHostPkg().equals(pkg)) {
			return method.invoke(who, args);
		}
		if (BlackList.isBlackPkg(pkg)) {
			return null;
		}
		PackageInfo packageInfo = (PackageInfo) method.invoke(who, args);
		if (packageInfo != null) {
			AppInfo appInfo = findAppInfo(pkg);
			if (appInfo != null) {
				ApplicationInfo info = packageInfo.applicationInfo;
				info.dataDir = appInfo.dataDir;
				info.uid = Process.myUid();
				info.nativeLibraryDir = appInfo.libDir;
			}
			return packageInfo;
		}
		return LocalPackageManager.getInstance().getPackageInfo(pkg, flags);
	}
}
