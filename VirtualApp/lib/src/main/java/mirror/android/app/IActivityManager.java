package mirror.android.app;

import android.content.pm.ProviderInfo;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcelable;

import mirror.RefBoolean;
import mirror.RefClass;
import mirror.RefConstructor;
import mirror.RefObject;
import mirror.RefMethod;
import mirror.MethodParams;
import mirror.RefStaticObject;

public class IActivityManager {
    public static Class<?> TYPE = RefClass.load(IActivityManager.class, "android.app.IActivityManager");
    @MethodParams({IBinder.class, boolean.class})
    public static RefMethod<Integer> getTaskForActivity;
    @MethodParams({IBinder.class, int.class})
    public static RefMethod<Void> setRequestedOrientation;
    @MethodParams({IBinder.class, String.class, int.class, int.class})
    public static RefMethod<Void> overridePendingTransition;
    public static RefMethod<Integer> startActivity;

    public static class ContentProviderHolder {
        @MethodParams(ProviderInfo.class)
        public static RefConstructor<Object> ctor;
        public static RefStaticObject<Parcelable.Creator> CREATOR;
        public static Class<?> TYPE = RefClass.load(ContentProviderHolder.class, "android.app.IActivityManager$ContentProviderHolder");
        public static RefObject<ProviderInfo> info;
        public static RefObject<IInterface> provider;
        public static RefBoolean noReleaseNeeded;
    }
}
