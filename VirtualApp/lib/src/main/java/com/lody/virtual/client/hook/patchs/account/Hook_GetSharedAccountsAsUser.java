package com.lody.virtual.client.hook.patchs.account;

import android.accounts.Account;

import com.lody.virtual.client.hook.base.Hook;

import java.lang.reflect.Method;

/**
 * @author Lody
 */

public class Hook_GetSharedAccountsAsUser extends Hook {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Object onHook(Object who, Method method, Object... args) throws Throwable {
        return new Account[0];
    }
}
