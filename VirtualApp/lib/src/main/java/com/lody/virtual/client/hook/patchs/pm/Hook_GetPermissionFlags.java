package com.lody.virtual.client.hook.patchs.pm;

import android.annotation.TargetApi;
import android.os.Build;

import com.lody.virtual.client.hook.base.Hook;

import java.lang.reflect.Method;

/**
 * @author Lody
 *
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
/* package */ class Hook_GetPermissionFlags extends Hook {

	@Override
	public String getName() {
		return "getPermissionFlags";
	}

	@Override
	public Object onHook(Object who, Method method, Object... args) throws Throwable {
		// TODO
		return method.invoke(who, args);
	}

}
