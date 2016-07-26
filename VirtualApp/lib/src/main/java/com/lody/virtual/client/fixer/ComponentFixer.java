package com.lody.virtual.client.fixer;

import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;

import com.lody.virtual.helper.proto.AppInfo;

/**
 * @author Lody
 */

public class ComponentFixer {

    public static void fixApplicationInfo(AppInfo info, ApplicationInfo applicationInfo) {
        if (TextUtils.isEmpty(applicationInfo.processName)) {
            applicationInfo.processName = applicationInfo.packageName;
        }
        applicationInfo.name = fixComponentClassName(info.packageName, applicationInfo.name);
        applicationInfo.publicSourceDir = info.apkPath;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
            applicationInfo.flags &= -ApplicationInfo.FLAG_STOPPED;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                applicationInfo.splitSourceDirs = new String[]{info.apkPath};
                applicationInfo.splitPublicSourceDirs = applicationInfo.splitSourceDirs;
            }
        } catch (Throwable e) {
            // ignore
        }
        try {
            applicationInfo.scanSourceDir = applicationInfo.dataDir;
            applicationInfo.scanPublicSourceDir = applicationInfo.dataDir;
        } catch (Throwable e) {
            // Ignore
        }
        applicationInfo.sourceDir = info.apkPath;
        applicationInfo.dataDir = info.dataDir;
        applicationInfo.enabled = true;
        applicationInfo.nativeLibraryDir = info.libDir;
        applicationInfo.uid = Process.myUid();
    }

    public static String fixComponentClassName(String pkgName, String className) {
        if (className != null) {
            if (className.charAt(0) == '.') {
                return pkgName + className;
            }
            return className;
        }
        return null;
    }
}
