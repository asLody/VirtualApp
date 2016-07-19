package com.lody.virtual.client.local;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.IBinder;
import android.os.RemoteException;

import com.lody.virtual.client.env.RuntimeEnv;
import com.lody.virtual.client.service.ServiceManagerNative;
import com.lody.virtual.helper.ExtraConstants;
import com.lody.virtual.helper.proto.AppTaskInfo;
import com.lody.virtual.helper.proto.VActRedirectResult;
import com.lody.virtual.helper.proto.VRedirectActRequest;
import com.lody.virtual.helper.utils.Reflect;
import com.lody.virtual.service.IActivityManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lody
 */
public class LocalActivityManager {

    private IActivityManager service;

    private Map<IBinder, LocalActivityRecord> mActivities = new HashMap<IBinder, LocalActivityRecord>(6);

    public Map<IBinder, LocalActivityRecord> getActivities() {
        return mActivities;
    }

    public static LocalActivityManager getInstance() {
        return Holder.sAM;
    }

    public IActivityManager getService() {
        if (service == null) {
            service = IActivityManager.Stub
                    .asInterface(ServiceManagerNative.getService(ServiceManagerNative.ACTIVITY_MANAGER));
        }
        return service;
    }

    public VActRedirectResult redirectTargetActivity(VRedirectActRequest request) {
        try {
            return getService().redirectTargetActivity(request);
        } catch (RemoteException e) {
            return RuntimeEnv.crash(e);
        }
    }

    public LocalActivityRecord onActivityCreate(Activity activity) {
        Intent intent = activity.getIntent();
        if (intent == null) {
            return null;
        }
        ActivityInfo activityInfo = intent.getParcelableExtra(ExtraConstants.EXTRA_TARGET_ACT_INFO);
        //此处在使用LocalActivityManager启动Activity的时候是空的,因为走不到replaceIntent里,
        // 比如掌阅会崩溃,暂时从Activity里取,没调研兼容性=_=,先用着
        if (activityInfo == null) {
            try {
                activityInfo = Reflect.on(activity).field("mActivityInfo").get();
            }catch(Exception ex){}
        }

        IBinder token = activity.getActivityToken();
        LocalActivityRecord r = new LocalActivityRecord();
        r.activityInfo = activityInfo;
        r.activity = activity;
        r.targetIntent = intent;
        mActivities.put(token, r);
        try {
            getService().onActivityCreated(activity.getActivityToken(), activityInfo, activity.getTaskId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return r;
    }

    public LocalActivityRecord onActivityResumed(Activity activity) {
        IBinder token = activity.getActivityToken();
        try {
            getService().onActivityResumed(token);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return mActivities.get(token);
    }

    public LocalActivityRecord onActivityDestroy(Activity activity) {
        IBinder token = activity.getActivityToken();
        try {
            getService().onActivityDestroyed(token);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return mActivities.remove(token);
    }

    public AppTaskInfo getTaskInfo(int taskId) {
        try {
            return getService().getTaskInfo(taskId);
        } catch (RemoteException e) {
            return RuntimeEnv.crash(e);
        }
    }

    private static final class Holder {
        private static final LocalActivityManager sAM = new LocalActivityManager();
    }
}
