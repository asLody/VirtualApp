package com.lody.virtual;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.lody.virtual.client.core.InstallStrategy;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.server.pm.VAppManagerService;

import java.util.Arrays;
import java.util.List;

/**
 * @author Lody
 */
public class GmsSupport {

    public static boolean isGmsFamilyPackage(String packageName) {
        for (String pkg:GOOGLE_PACKAGES){
            if(TextUtils.equals(pkg,packageName)){
                return true;
            }
        }
        return packageName.equals("com.android.vending");
    }

    public static boolean isGoogleFrameworkInstalled() {
        return VirtualCore.get().isAppInstalled("com.google.android.gms");
    }

    public static String[] GOOGLE_PACKAGES = {"com.google.android.gms","com.google.android.gsf","com.google.android.gsf.login"};
}