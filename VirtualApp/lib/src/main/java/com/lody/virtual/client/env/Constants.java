package com.lody.virtual.client.env;

import android.content.Intent;

import com.lody.virtual.client.stub.ShortcutHandleActivity;

/**
 * @author Lody
 *
 */
public class Constants {

	public static final String EXTRA_USER_HANDLE = "android.intent.extra.user_handle";
	public static String META_KEY_IDENTITY = "X-Identity";

	public static String META_VALUE_STUB = "Stub-User";

	/**
	 * Server process name of VA
	 */
	public static String SERVER_PROCESS_NAME = ":x";
	/**
	 * Install shortcut action
	 */
	public static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
	/**
	 * Uninstall shortcut action
	 */
	public static final String ACTION_UNINSTALL_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";
	/**
	 * Package name of System-UI.apk
	 */
	public static final String SYSTEM_UI_PKG = "com.android.systemui";
	/**
	 * If an apk declared the "fake-signature" attribute on its Application TAG,
	 * we will use its signature instead of the real signature.
	 *
	 * For more detail, please see :
	 * https://github.com/microg/android_packages_apps_GmsCore/blob/master/
	 * patches/android_frameworks_base-M.patch.
	 */
	public static final String FEATURE_FAKE_SIGNATURE = "fake-signature";
	public static final String ACTION_PACKAGE_ADDED = "virtual." + Intent.ACTION_PACKAGE_ADDED;
	public static final String ACTION_PACKAGE_REMOVED = "virtual." + Intent.ACTION_PACKAGE_REMOVED;
	public static final String ACTION_PACKAGE_CHANGED = "virtual." + Intent.ACTION_PACKAGE_CHANGED;
	/**
	 * The activity who handle the shortcut.
	 */
	public static String SHORTCUT_PROXY_ACTIVITY_NAME = ShortcutHandleActivity.class.getName();

	public static String ACTION_INSTALL_PACKAGE = "android.intent.action.VIRTUAL_INSTALL_PACKAGE";

	public static String ACTION_UNINSTALL_PACKAGE = "android.intent.action.VIRTUAL_UNINSTALL_PACKAGE";
	
	public static final String ACTION_USER_ADDED = "virtual." + "android.intent.action.USER_ADDED";
	public static final String ACTION_USER_REMOVED = "virtual." + "android.intent.action.USER_REMOVED";
	public static final String ACTION_USER_INFO_CHANGED = "virtual." + "android.intent.action.USER_CHANGED";

	public static final String ACTION_USER_STARTED = "Virtual." + "android.intent.action.USER_STARTED";
}
