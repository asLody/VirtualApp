package com.lody.virtual.client.ipc;

import android.os.Binder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Lody
 */

public class LocalProxyUtils {

    /**
     * Generates the Proxy instance for a base object, each IPC call will clean its calling identity.
     * @param interfaceClass interface class
     * @param base base object
     * @return proxy object
     */
    public static <T> T genProxy(Class<T> interfaceClass, final Object base) {
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{ interfaceClass }, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                long identity = Binder.clearCallingIdentity();
                try {
                    return method.invoke(base, args);
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }
        });
    }
}
