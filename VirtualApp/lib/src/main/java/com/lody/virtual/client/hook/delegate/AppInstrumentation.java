package com.lody.virtual.client.hook.delegate;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.fixer.ActivityFixer;
import com.lody.virtual.client.fixer.ContextFixer;
import com.lody.virtual.client.interfaces.Injectable;
import com.lody.virtual.client.local.ActivityClientRecord;
import com.lody.virtual.client.local.VActivityManager;
import com.lody.virtual.helper.compat.ActivityManagerCompat;
import com.lody.virtual.helper.compat.BundleCompat;

import mirror.android.app.ActivityThread;

/**
 * @author Lody
 */
public final class AppInstrumentation extends InstrumentationDelegate implements Injectable {

	private static final String TAG = AppInstrumentation.class.getSimpleName();
	private static AppInstrumentation gDefault;
	private ActivityDelegate activityDelegate;

	private AppInstrumentation(Instrumentation base) {
		super(base);
		activityDelegate = VirtualCore.get().getActivityDelegate();
	}

	public static AppInstrumentation getDefault() {
		if (gDefault == null) {
			synchronized (AppInstrumentation.class) {
				if (gDefault == null) {
					gDefault = create();
				}
			}
		}
		return gDefault;
	}

	private static AppInstrumentation create() {
		Instrumentation instrumentation = ActivityThread.mInstrumentation.get(VirtualCore.mainThread());
		if (instrumentation instanceof AppInstrumentation) {
			return (AppInstrumentation) instrumentation;
		}
		return new AppInstrumentation(instrumentation);
	}


	@Override
	public void inject() throws Throwable {
		ActivityThread.mInstrumentation.set(VirtualCore.mainThread(), this);
	}

	@Override
	public boolean isEnvBad() {
		return ActivityThread.mInstrumentation.get(VirtualCore.mainThread()) != this;
	}

	@Override
	public void callActivityOnCreate(Activity activity, Bundle icicle) {
		IBinder token = mirror.android.app.Activity.mToken.get(activity);
		ActivityClientRecord r = VActivityManager.get().getActivityRecord(token);
		if (r != null) {
            r.activity = activity;
        }
		ContextFixer.fixContext(activity);
		ActivityFixer.fixActivity(activity);
		ActivityInfo info = null;
		if (r != null) {
            info = r.info;
        }
		if (info != null) {
            if (info.theme != 0) {
                activity.setTheme(info.theme);
            }
            if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    && info.screenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                activity.setRequestedOrientation(info.screenOrientation);
            }
        }
		super.callActivityOnCreate(activity, icicle);
		if(activityDelegate!=null){
			activityDelegate.onActivityCreate(activity);
		}
	}

	@Override
	public void callActivityOnResume(Activity activity) {
		VActivityManager.get().onActivityResumed(activity);
		super.callActivityOnResume(activity);
		Intent intent = activity.getIntent();
		Bundle bundle = intent.getBundleExtra("_VA_|_sender_");
		if (bundle != null) {
			IBinder loadingPageToken = BundleCompat.getBinder(bundle, "_VA_|_loading_token_");
			ActivityManagerCompat.finishActivity(loadingPageToken, -1, null);
		}
		if(activityDelegate!=null){
			activityDelegate.onActivityResumed(activity);
		}
	}

	@Override
	public void callActivityOnDestroy(Activity activity) {
		super.callActivityOnDestroy(activity);
		if(activityDelegate!=null){
			activityDelegate.onActivityDestroy(activity);
		}
	}

	@Override
	public void callActivityOnPause(Activity activity) {
		super.callActivityOnPause(activity);
		if(activityDelegate!=null){
			activityDelegate.onActivityPaused(activity);
		}
	}

	@Override
	public void callApplicationOnCreate(Application app) {
		super.callApplicationOnCreate(app);
	}

}
