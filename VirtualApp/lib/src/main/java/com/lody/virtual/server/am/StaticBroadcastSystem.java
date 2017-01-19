package com.lody.virtual.server.am;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageParser;
import android.os.Handler;

import com.lody.virtual.client.env.SpecialComponentList;
import com.lody.virtual.helper.proto.AppSetting;
import com.lody.virtual.helper.utils.collection.ArrayMap;
import com.lody.virtual.server.pm.VAppManagerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import mirror.android.app.ContextImpl;
import mirror.android.app.LoadedApkHuaWei;
import mirror.android.rms.resource.ReceiverResourceLP;
import mirror.android.rms.resource.ReceiverResourceM;

import static android.content.Intent.FLAG_RECEIVER_REGISTERED_ONLY;

/**
 * @author Lody
 */

public class StaticBroadcastSystem {

    private final ArrayMap<String, List<BroadcastReceiver>> mReceivers = new ArrayMap<>();
    private final Context mContext;
    private final StaticScheduler mScheduler;
    private final VActivityManagerService mAMS;
    private final VAppManagerService mApp;

    public StaticBroadcastSystem(Context context, VActivityManagerService ams, VAppManagerService app) {
        this.mContext = context;
        this.mApp = app;
        this.mAMS = ams;
        mScheduler = new StaticScheduler();
    }


    public void initialize() {
        fuckHuaWeiVerifier();
    }

    private void fuckHuaWeiVerifier() {
        /*
        FIX ISSUE #171:
        java.lang.AssertionError: Register too many Broadcast Receivers
        at android.app.LoadedApk.checkRecevierRegisteredLeakLocked(LoadedApk.java:772)
        at android.app.LoadedApk.getReceiverDispatcher(LoadedApk.java:800)
        at android.app.ContextImpl.registerReceiverInternal(ContextImpl.java:1329)
        at android.app.ContextImpl.registerReceiver(ContextImpl.java:1309)
        at com.lody.virtual.server.am.StaticBroadcastSystem.startApp(StaticBroadcastSystem.java:54)
        at com.lody.virtual.server.pm.VAppManagerService.install(VAppManagerService.java:193)
        at com.lody.virtual.server.pm.VAppManagerService.preloadAllApps(VAppManagerService.java:98)
        at com.lody.virtual.server.pm.VAppManagerService.systemReady(VAppManagerService.java:70)
        at com.lody.virtual.server.BinderProvider.onCreate(BinderProvider.java:42)
        */
        if (LoadedApkHuaWei.mReceiverResource != null) {
            Object packageInfo = ContextImpl.mPackageInfo.get(mContext);
            if (packageInfo != null) {
                Object receiverResource = LoadedApkHuaWei.mReceiverResource.get(packageInfo);
                if (receiverResource != null) {
                    if (ReceiverResourceM.mWhiteList != null) {
                        String[] whiteList = ReceiverResourceM.mWhiteList.get(receiverResource);
                        List<String> newWhiteList = new LinkedList<>();
                        Collections.addAll(newWhiteList, whiteList);
                        // Add our package name to the white list.
                        newWhiteList.add(mContext.getPackageName());
                        ReceiverResourceM.mWhiteList.set(receiverResource, newWhiteList.toArray(new String[newWhiteList.size()]));
                    } else if (ReceiverResourceLP.mResourceConfig != null) {
                        // Just clear the ResourceConfig.
                        ReceiverResourceLP.mResourceConfig.set(receiverResource, null);
                    }
                }
            }
        }
    }

    public void startApp(PackageParser.Package p) {
        AppSetting setting = (AppSetting) p.mExtras;
        for (PackageParser.Activity receiver : p.receivers) {
            ActivityInfo info = receiver.info;
            List<? extends IntentFilter> filters = receiver.intents;
            List<BroadcastReceiver> receivers = mReceivers.get(p.packageName);
            if (receivers == null) {
                receivers = new ArrayList<>();
                mReceivers.put(p.packageName, receivers);
            }
            String componentAction = String.format("_VA_%s_%s", info.packageName, info.name);
            IntentFilter componentFilter = new IntentFilter(componentAction);
            BroadcastReceiver r = new StaticBroadcastReceiver(setting.appId, info, componentFilter);
            mContext.registerReceiver(r, componentFilter, null, mScheduler);
            receivers.add(r);
            for (IntentFilter filter : filters) {
                IntentFilter cloneFilter = new IntentFilter(filter);
                redirectFilterActions(cloneFilter);
                r = new StaticBroadcastReceiver(setting.appId, info, cloneFilter);
                mContext.registerReceiver(r, cloneFilter, null, mScheduler);
                receivers.add(r);
            }
        }
    }

    private void redirectFilterActions(IntentFilter filter) {
        List<String> actions = mirror.android.content.IntentFilter.mActions.get(filter);
        ListIterator<String> iterator = actions.listIterator();
        while (iterator.hasNext()) {
            String action = iterator.next();
            if (SpecialComponentList.isActionInBlackList(action)) {
                iterator.remove();
                continue;
            }
            String protectedAction = SpecialComponentList.protectAction(action);
            if (protectedAction != null) {
                iterator.set(protectedAction);
            }
        }
    }

    public void stopApp(String packageName) {
        List<BroadcastReceiver> receivers = mReceivers.get(packageName);
        if (receivers != null) {
            for (BroadcastReceiver r : receivers) {
                mContext.unregisterReceiver(r);
            }
        }
        mReceivers.remove(packageName);
    }

    private static final class StaticScheduler extends Handler {

    }

    private final class StaticBroadcastReceiver extends BroadcastReceiver {
        private int appId;
        private ActivityInfo info;
        @SuppressWarnings("unused")
        private IntentFilter filter;

        private StaticBroadcastReceiver(int appId, ActivityInfo info, IntentFilter filter) {
            this.appId = appId;
            this.info = info;
            this.filter = filter;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mApp.isBooting()) {
                return;
            }
            if ((intent.getFlags() & FLAG_RECEIVER_REGISTERED_ONLY) != 0) {
                return;
            }
            PendingResult result = goAsync();
            synchronized (mAMS) {
                if (!mAMS.handleStaticBroadcast(appId, info, intent, this, result)) {
                    result.finish();
                }
            }
        }
    }
}
