package com.lody.virtual.client.hook.patchs.window;

import com.lody.virtual.client.hook.base.Hook;

import java.lang.reflect.Method;

/**
 * @author Lody
 *
 */
/* package */ class Hook_OverridePendingAppTransitionInPlace extends Hook {

	@Override
	public String getName() {
		return "overridePendingAppTransitionInPlace";
	}

	@Override
	public Object onHook(Object who, Method method, Object... args) throws Throwable {
		String pkgName = (String) args[0];
		if (isAppPkg(pkgName)) {
			args[0] = getHostPkg();
		}
		return method.invoke(who, args);
	}
}
