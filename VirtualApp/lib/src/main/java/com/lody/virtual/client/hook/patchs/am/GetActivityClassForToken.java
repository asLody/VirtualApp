package com.lody.virtual.client.hook.patchs.am;

import android.os.IBinder;

import com.lody.virtual.client.hook.base.Hook;
import com.lody.virtual.client.ipc.VActivityManager;

import java.lang.reflect.Method;

/**
 * @author Lody
 *
 */
/* package */ class GetActivityClassForToken extends Hook {

	@Override
	public String getName() {
		return "getActivityClassForToken";
	}

	@Override
	public Object call(Object who, Method method, Object... args) throws Throwable {
		IBinder token = (IBinder) args[0];
		return VActivityManager.get().getActivityForToken(token);
	}

	@Override
	public boolean isEnable() {
		return isAppProcess();
	}
}
