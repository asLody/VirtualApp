package com.lody.virtual.client.service;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.RuntimeEnv;
import com.lody.virtual.helper.ExtraConstants;
import com.lody.virtual.helper.MethodConstants;
import com.lody.virtual.helper.compat.BundleCompat;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.service.ServiceCache;
import com.lody.virtual.service.interfaces.IServiceFetcher;

/**
 * @author Lody
 *
 */
public class ServiceManagerNative {

	public static final String PACKAGE_MANAGER = "package";
	public static final String ACTIVITY_MANAGER = "activity";
	public static final String APP_MANAGER = "app";
	public static final String PROCESS_MANAGER = "process";
	public static final String SERVICE_MANAGER = "service";
	public static final String CONTENT_MANAGER = "content";
	public static final String ACCOUNT_MANAGER = "account";
	private static final String TAG = ServiceManagerNative.class.getSimpleName();
	private static final String SERVICE_CP_AUTH = "virtual.service.BinderProvider";

	public static IServiceFetcher getServiceFetcher() {
		Context context = VirtualCore.getCore().getContext();
		Bundle response = new ProviderCaller.Builder(context, SERVICE_CP_AUTH).methodName("@").call();
		if (response != null) {
			IBinder binder = BundleCompat.getBinder(response, ExtraConstants.EXTRA_BINDER);
			linkBinderDied(binder);
			return IServiceFetcher.Stub.asInterface(binder);
		}
		return null;
	}

	private static void linkBinderDied(final IBinder binder) {
		IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
			@Override
			public void binderDied() {
				binder.unlinkToDeath(this, 0);
				VLog.e(TAG, "Ops, the server has crashed.");
				RuntimeEnv.exit();
			}
		};
		try {
			binder.linkToDeath(deathRecipient, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static IBinder getService(String name) {
		if (VirtualCore.getCore().isServiceProcess()) {
			return ServiceCache.getService(name);
		}
		IServiceFetcher fetcher = getServiceFetcher();
		if (fetcher != null) {
			try {
				return fetcher.getService(name);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		VLog.e(TAG, "GetService(%s) return null.", name);
		return null;
	}

	public static void addService(String name, IBinder service) {
		IServiceFetcher fetcher = getServiceFetcher();
		if (fetcher != null) {
			try {
				fetcher.addService(name, service);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	public static void removeService(String name) {
		IServiceFetcher fetcher = getServiceFetcher();
		if (fetcher != null) {
			try {
				fetcher.removeService(name);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public static void startup(Context context) {
		new ProviderCaller.Builder(context, SERVICE_CP_AUTH).methodName(MethodConstants.INIT_SERVICE).call();
	}

}
