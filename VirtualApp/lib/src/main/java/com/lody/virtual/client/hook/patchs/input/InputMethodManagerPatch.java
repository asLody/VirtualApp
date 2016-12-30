package com.lody.virtual.client.hook.patchs.input;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.Patch;
import com.lody.virtual.client.hook.base.PatchBinderDelegate;

import mirror.com.android.internal.view.inputmethod.InputMethodManager;

/**
 * @author Lody
 */
@Patch({StartInput.class, WindowGainedFocus.class})
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)

public class InputMethodManagerPatch extends PatchBinderDelegate {

	public InputMethodManagerPatch() {
		super(
				InputMethodManager.mService.get(
						VirtualCore.get().getContext().getSystemService(Context.INPUT_METHOD_SERVICE)),
				Context.INPUT_METHOD_SERVICE);
	}

	@Override
	public void inject() throws Throwable {
		Object inputMethodManager = getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		InputMethodManager.mService.set(inputMethodManager, getHookDelegate().getProxyInterface());
		getHookDelegate().replaceService(Context.INPUT_METHOD_SERVICE);
	}


	@Override
	public boolean isEnvBad() {
		Object inputMethodManager = getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		return InputMethodManager
				.mService.get(inputMethodManager) != getHookDelegate().getBaseInterface();
	}

}