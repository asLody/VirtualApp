package com.lody.virtual.client.hook.base;

import android.content.Context;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.interfaces.Injectable;

import java.lang.reflect.Constructor;

/**
 * @author Lody
 *         <p>
 *         This class is responsible with:
 *         - Instantiating a {@link HookDelegate.HookHandler} on {@link #getHookDelegate()} ()}
 *         - Install a bunch of {@link Hook}s, either with a @{@link Patch} annotation or manually
 *         calling {@link #addHook(Hook)} from {@link #onBindHooks()}
 *         - Install the hooked object on the Runtime via {@link #inject()}
 *         <p>
 *         All {@link PatchDelegate}s (plus a couple of other @{@link Injectable}s are installed by
 *         {@link com.lody.virtual.client.core.PatchManager}
 * @see Patch
 */
public abstract class PatchDelegate<T extends HookDelegate> implements Injectable {

    protected T hookDelegate;

    public PatchDelegate(T hookDelegate) {
        this.hookDelegate = hookDelegate;
        onBindHooks();
        afterHookApply(hookDelegate);
    }

    protected void onBindHooks() {

        if (hookDelegate == null) {
            return;
        }

        Class<? extends PatchDelegate> clazz = getClass();
        Patch patch = clazz.getAnnotation(Patch.class);
        if (patch != null) {
            Class<?>[] hookTypes = patch.value();
            for (Class<?> hookType : hookTypes) {
                addHook(hookType);
            }

        }
    }

    private void addHook(Class<?> hookType) {
        try {
            Constructor<?> constructor = hookType.getDeclaredConstructors()[0];
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            Hook hook;
            if (constructor.getParameterTypes().length == 0) {
                hook = (Hook) constructor.newInstance();
            } else {
                hook = (Hook) constructor.newInstance(this);
            }
            hookDelegate.addHook(hook);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to instance Hook : " + hookType + " : " + e.getMessage());
        }
    }

    public Hook addHook(Hook hook) {
        return hookDelegate.addHook(hook);
    }

    protected void afterHookApply(T delegate) {
    }

    @Override
    public abstract void inject() throws Throwable;

    public Context getContext() {
        return VirtualCore.get().getContext();
    }

    public T getHookDelegate() {
        return hookDelegate;
    }
}
