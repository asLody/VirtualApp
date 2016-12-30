package com.lody.virtual.client.hook.patchs.am;

import com.lody.virtual.client.hook.base.Hook;

import java.lang.reflect.Method;

/**
 * @author Lody
 */

public class Hook_UnstableProviderDied  extends Hook {

    @Override
    public String getName() {
        return "unstableProviderDied";
    }

    @Override
    public Object onHook(Object who, Method method, Object... args) throws Throwable {
        if (args[0] == null) {
            return 0;
        }
        return method.invoke(who, args);
    }
}
