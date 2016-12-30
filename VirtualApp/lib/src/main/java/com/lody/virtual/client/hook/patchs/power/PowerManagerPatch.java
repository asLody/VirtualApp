package com.lody.virtual.client.hook.patchs.power;

import android.content.Context;
import android.os.IPowerManager;
import android.os.ServiceManager;

import com.lody.virtual.client.hook.base.HookBinder;
import com.lody.virtual.client.hook.base.PatchObject;
import com.lody.virtual.client.hook.base.ReplaceLastPkgHook;
import com.lody.virtual.client.hook.base.ReplaceSequencePkgHook;
import com.lody.virtual.client.hook.binders.HookPowerBinder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Lody
 *
 *
 * @see IPowerManager
 */
public class PowerManagerPatch extends PatchObject<IPowerManager, HookPowerBinder> {

	@Override
	protected HookPowerBinder initHookObject() {
		return new HookPowerBinder();
	}

	@Override
	public void inject() throws Throwable {
		HookBinder<IPowerManager> hookBinder = getHookObject();
		hookBinder.injectService(Context.POWER_SERVICE);
	}

	@Override
	protected void applyHooks() {
		super.applyHooks();
		addHook(new ReplaceSequencePkgHook("acquireWakeLock", 2) {

			@Override
			public Object onHook(Object who, Method method, Object... args) throws Throwable {
				try {
					return super.onHook(who, method, args);
				} catch (InvocationTargetException e) {
					return onHandleError(e);
				}
			}
		});
		addHook(new ReplaceLastPkgHook("acquireWakeLockWithUid") {

			@Override
			public Object onHook(Object who, Method method, Object... args) throws Throwable {
				try {
					return super.onHook(who, method, args);
				} catch (InvocationTargetException e) {
					return onHandleError(e);
				}
			}

		});
	}

	private Object onHandleError(InvocationTargetException e) throws Throwable {
		if (e.getCause() instanceof SecurityException) {
			return 0;
		}
		throw e.getCause();
	}

	@Override
	public boolean isEnvBad() {
		return ServiceManager.getService(Context.POWER_SERVICE) != getHookObject();
	}
}
