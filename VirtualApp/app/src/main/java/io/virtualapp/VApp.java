package io.virtualapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;

import com.flurry.android.FlurryAgent;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.stub.VASettings;

import io.virtualapp.delegate.MyAppRequestListener;
import io.virtualapp.delegate.MyComponentDelegate;
import io.virtualapp.delegate.MyPhoneInfoDelegate;
import io.virtualapp.delegate.MyTaskDescriptionDelegate;
import jonathanfinerty.once.Once;
import com.flurry.android.FlurryAgentListener;
import me.weishu.reflection.Reflection;
import android.provider.Settings;

/**
 * @author Lody
 */
public class VApp extends MultiDexApplication {

    private static VApp gApp;
    private SharedPreferences mPreferences;

    public static VApp getApp() {
        return gApp;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mPreferences = base.getSharedPreferences("va", Context.MODE_MULTI_PROCESS);
        VASettings.ENABLE_IO_REDIRECT = true;
        VASettings.ENABLE_INNER_SHORTCUT = false;
		
		try
		{
            Reflection.unseal(base);
			// Settings.Global.putInt("hidden_api_policy_pre_p_apps", 0);
        }
		catch (Throwable e)
		{
            e.printStackTrace();
        }
	
		try
		{
            VirtualCore.get().startup(base);
        }
		catch (Throwable e)
		{
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        gApp = this;
        super.onCreate();
        final VirtualCore virtualCore = VirtualCore.get();
        virtualCore.initialize(new VirtualCore.VirtualInitializer() {
				@Override
				public void onMainProcess() {
					Once.initialise(VApp.this);
					new FlurryAgent.Builder()
                        .withLogEnabled(true)
						.withListener(new FlurryAgentListener() {
							@Override
							public void onSessionStarted() {

							}
						})
                        .build(VApp.this, "48RJJP7ZCZZBB6KMMWW5");
				}

				@Override
				public void onVirtualProcess() {
					//listener components
					virtualCore.setComponentDelegate(new MyComponentDelegate());
					//fake phone imei,macAddress,BluetoothAddress
					virtualCore.setPhoneInfoDelegate(new MyPhoneInfoDelegate());
					//fake task description's icon and title
					virtualCore.setTaskDescriptionDelegate(new MyTaskDescriptionDelegate());
				}

				@Override
				public void onServerProcess() {
					virtualCore.setAppRequestListener(new MyAppRequestListener(VApp.this));
					virtualCore.addVisibleOutsidePackage("com.tencent.mobileqq");
					virtualCore.addVisibleOutsidePackage("com.tencent.mobileqqi");
					virtualCore.addVisibleOutsidePackage("com.tencent.minihd.qq");
					virtualCore.addVisibleOutsidePackage("com.tencent.qqlite");
					virtualCore.addVisibleOutsidePackage("com.facebook.katana");
					virtualCore.addVisibleOutsidePackage("com.whatsapp");
					virtualCore.addVisibleOutsidePackage("com.tencent.mm");
					virtualCore.addVisibleOutsidePackage("com.immomo.momo");
				}
			});
    }

    public static SharedPreferences getPreferences() {
        return getApp().mPreferences;
    }

}
