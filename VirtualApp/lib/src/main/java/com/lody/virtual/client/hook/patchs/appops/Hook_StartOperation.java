package com.lody.virtual.client.hook.patchs.appops;

import com.lody.virtual.client.hook.base.Hook;

import java.lang.reflect.Method;

/**
 * @author Lody
 *
 */
/* package */ class Hook_StartOperation extends Hook {

	@Override
	public String getName() {
		return "startOperation";
	}

	@Override
	public Object onHook(Object who, Method method, Object... args) throws Throwable {

		String pkgName = (String) args[3];
		if (isAppPkg(pkgName)) {
			args[3] = getHostPkg();
		}
		return method.invoke(who, args);
	}
}
