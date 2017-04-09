package com.lody.virtual.client.hook.proxies.notification;

import android.app.Notification;
import android.os.Build;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import com.lody.virtual.client.ipc.VNotificationManager;
import com.lody.virtual.helper.utils.ArrayUtils;
import com.lody.virtual.helper.utils.VLog;

import java.lang.reflect.Method;

/**
 * @author Lody
 */

class MethodProxies {

    static class EnqueueNotification extends MethodProxy {

        @Override
        public String getMethodName() {
            return "enqueueNotification";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            //enqueueNotification(pkg, id, notification, idOut);
            String pkg = (String) args[0];
            int notificationIndex = ArrayUtils.indexOfFirst(args, Notification.class);
            int idIndex = ArrayUtils.indexOfFirst(args, Integer.class);
            int id = (int) args[idIndex];
            id = VNotificationManager.get().dealNotificationId(id, pkg, null, getAppUserId());
            args[idIndex] = id;
            Notification notification = (Notification) args[notificationIndex];
            if (!VNotificationManager.get().dealNotification(id, notification, pkg)) {
                return 0;
            }
            VNotificationManager.get().addNotification(id, null, pkg, getAppUserId());
            args[0] = getHostPkg();
            return method.invoke(who, args);
        }
    }

    /* package */ static class EnqueueNotificationWithTag extends MethodProxy {

        @Override
        public String getMethodName() {
            return "enqueueNotificationWithTag";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            //15 enqueueNotificationWithTag(pkg, tag, id, notification, idOut);
            //16 enqueueNotificationWithTag(pkg, tag, id, notification, idOut);
            //17 enqueueNotificationWithTag(pkg, tag, id, notification, idOut, UserHandle.myUserId());
            //18 enqueueNotificationWithTag(pkg, mContext.getBasePackageName(), tag, id, notification, idOut, UserHandle.myUserId());
            //19 enqueueNotificationWithTag(pkg, mContext.getOpPackageName(), tag, id, notification, idOut, UserHandle.myUserId());
            //21 enqueueNotificationWithTag(pkg, mContext.getOpPackageName(), tag, id, notification, idOut, UserHandle.myUserId());
            //22 enqueueNotificationWithTag(pkg, mContext.getOpPackageName(), tag, id, notification, idOut, UserHandle.myUserId());
            //23 enqueueNotificationWithTag(pkg, mContext.getOpPackageName(), tag, id, notification, idOut, UserHandle.myUserId());
            //24 enqueueNotificationWithTag(pkg, mContext.getOpPackageName(), tag, id, notification, idOut, user.getIdentifier());
            //25 enqueueNotificationWithTag(pkg, mContext.getOpPackageName(), tag, id, notification, idOut, user.getIdentifier());
            String pkg = (String) args[0];
            int notificationIndex = ArrayUtils.indexOfFirst(args, Notification.class);
            int idIndex = ArrayUtils.indexOfFirst(args, Integer.class);
            int tagIndex = (Build.VERSION.SDK_INT >= 18 ? 2 : 1);
            int id = (int) args[idIndex];
    //        int user = (Build.VERSION.SDK_INT>=17?((int)args[args.length-1]):0);
            String tag = (String) args[tagIndex];

            id = VNotificationManager.get().dealNotificationId(id, pkg, tag, getAppUserId());
            tag= VNotificationManager.get().dealNotificationTag(id, pkg, tag, getAppUserId());
            args[idIndex] = id;
            args[tagIndex] = tag;
            //key(tag,id)
            Notification notification = (Notification) args[notificationIndex];
            if (!VNotificationManager.get().dealNotification(id, notification, pkg)) {
                return 0;
            }
            VNotificationManager.get().addNotification(id, tag, pkg, getAppUserId());
            args[0] = getHostPkg();
            if (Build.VERSION.SDK_INT >= 18 && args[1] instanceof String) {
                args[1] = getHostPkg();
            }
            return method.invoke(who, args);
        }
    }

    /* package */ static class EnqueueNotificationWithTagPriority extends EnqueueNotificationWithTag {

        @Override
        public String getMethodName() {
            return "enqueueNotificationWithTagPriority";
        }
    }

    /* package */ static class CancelNotificationWithTag extends MethodProxy {

        @Override
        public String getMethodName() {
            return "cancelNotificationWithTag";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            //15 cancelNotificationWithTag(pkg, tag, id);
            //16 cancelNotificationWithTag(pkg, tag, id);
            //17 cancelNotificationWithTag(pkg, tag, id, UserHandle.myUserId());
            //18 cancelNotificationWithTag(pkg, tag, id, UserHandle.myUserId());
            //19 cancelNotificationWithTag(pkg, tag, id, UserHandle.myUserId());
            //21 cancelNotificationWithTag(pkg, tag, id, UserHandle.myUserId());
            //22 cancelNotificationWithTag(pkg, tag, id, UserHandle.myUserId());
            //23 cancelNotificationWithTag(pkg, tag, id, UserHandle.myUserId());
            //24 cancelNotificationWithTag(pkg, tag, id, user.getIdentifier());
            //25 cancelNotificationWithTag(pkg, tag, id, user.getIdentifier());
            String pkg = MethodParameterUtils.replaceFirstAppPkg(args);
            String tag = (String) args[1];
            int id = (int) args[2];
            id = VNotificationManager.get().dealNotificationId(id, pkg, tag, getAppUserId());
            tag = VNotificationManager.get().dealNotificationTag(id, pkg, tag, getAppUserId());

            args[1] = tag;
            args[2] = id;
            VLog.d("notification", "need cancel " + tag + " " + id);
            return method.invoke(who, args);
        }
    }

    /**
     * @author Lody
     */
    /* package */ static class CancelAllNotifications extends MethodProxy {

        @Override
        public String getMethodName() {
            return "cancelAllNotifications";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            String pkg = MethodParameterUtils.replaceFirstAppPkg(args);
    //        int user = 0;
    //        if (Build.VERSION.SDK_INT >= 17) {
    //            user = (int) args[1];
    //        }
            if (VirtualCore.get().isAppInstalled(pkg)) {
                VNotificationManager.get().cancelAllNotification(pkg, getAppUserId());
                return 0;
            }
            return method.invoke(who, args);
        }
    }

    static class AreNotificationsEnabledForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "areNotificationsEnabledForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            String pkg  = (String) args[0];
            return VNotificationManager.get().areNotificationsEnabledForPackage(pkg, getAppUserId());
    //        return super.call(who, method, args);
        }
    }

    static class SetNotificationsEnabledForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "setNotificationsEnabledForPackage";
        }
        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            String pkg  = (String) args[0];
            int enableIndex = ArrayUtils.indexOfFirst(args, Boolean.class);
            boolean enable = (boolean) args[enableIndex];
            VNotificationManager.get().setNotificationsEnabledForPackage(pkg, enable, getAppUserId());
            return 0;
        }
    }
}
