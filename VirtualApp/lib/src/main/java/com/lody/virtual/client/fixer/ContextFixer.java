package com.lody.virtual.client.fixer;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.DropBoxManager;

import com.lody.virtual.client.core.PatchManager;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.HookBinderDelegate;
import com.lody.virtual.client.hook.patchs.dropbox.DropBoxManagerPatch;
import com.lody.virtual.client.hook.patchs.graphics.GraphicsStatsPatch;
import com.lody.virtual.helper.utils.Reflect;
import com.lody.virtual.helper.utils.ReflectException;

import mirror.android.app.ContextImpl;
import mirror.android.app.ContextImplKitkat;
import mirror.android.content.ContentResolverJBMR2;

/**
 * @author Lody
 *
 */
public class ContextFixer {

	private static final String TAG = ContextFixer.class.getSimpleName();

	/**
	 * Fuck AppOps
	 *
	 * @param context
	 *            插件Context
	 */
	public static void fixContext(Context context) {
		PatchManager.getInstance().checkEnv(GraphicsStatsPatch.class);
		int deep = 0;
		while (context instanceof ContextWrapper) {
			context = ((ContextWrapper) context).getBaseContext();
			deep++;
			if (deep >= 10) {
				return;
			}
		}
		ContextImpl.mPackageManager.set(context, null);
		context.getPackageManager();
		if (!VirtualCore.get().isVAppProcess()) {
			return;
		}
		DropBoxManager dm = (DropBoxManager) context.getSystemService(Context.DROPBOX_SERVICE);
		HookBinderDelegate boxBinder = PatchManager.getInstance().getHookObject(DropBoxManagerPatch.class);
		if (boxBinder != null) {
			try {
				Reflect.on(dm).set("mService", boxBinder.getProxyInterface());
			} catch (ReflectException e) {
				e.printStackTrace();
			}
		}
		String hostPkg = VirtualCore.get().getHostPkg();
		ContextImpl.mBasePackageName.set(context, hostPkg);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			ContextImplKitkat.mOpPackageName.set(context, hostPkg);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			ContentResolverJBMR2.mPackageName.set(context.getContentResolver(), hostPkg);
		}
	}

}
