package mirror.android.app.job;

import android.annotation.TargetApi;
import android.content.Intent;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefConstructor;
import mirror.RefInt;
import mirror.RefMethod;
import mirror.RefObject;

@TargetApi(26)
public class JobWorkItem {
    public static Class<?> TYPE = RefClass.load(JobWorkItem.class, android.app.job.JobWorkItem.class);
    @MethodParams({Intent.class})
    public static RefConstructor<Object> ctor;
    public static RefMethod<Intent> getIntent;
    public static RefInt mDeliveryCount;
    public static RefObject<Object> mGrants;
    public static RefInt mWorkId;
}