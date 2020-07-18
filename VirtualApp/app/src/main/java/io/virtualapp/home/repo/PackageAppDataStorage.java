package io.virtualapp.home.repo;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.remote.InstalledAppInfo;

import java.util.HashMap;
import java.util.Map;

import io.virtualapp.VApp;
import io.virtualapp.abs.Callback;
import io.virtualapp.abs.ui.VUiKit;
import io.virtualapp.home.models.PackageAppData;
import org.jdeferred.DeferredRunnable;
import org.jdeferred.DoneCallback;

/**
 * @author Lody
 *         <p>
 *         Cache the loaded PackageAppData.
 */
public class PackageAppDataStorage {

    private static final PackageAppDataStorage STORAGE = new PackageAppDataStorage();
    private final Map<String, PackageAppData> packageDataMap = new HashMap<>();

    public static PackageAppDataStorage get() {
        return STORAGE;
    }

    public PackageAppData acquire(String packageName) {
        PackageAppData data;
        synchronized (packageDataMap)
		{
            data = packageDataMap.get(packageName);
            if (data == null)
			{
                data = loadAppData(packageName);
            }
        }
        return data;
    }

    public void acquire(final String packageName, final Callback<PackageAppData> callback) {
        VUiKit.defer()
			.when(new Runnable() {
				@Override
				public void run() {
					acquire(packageName);
				}
			}).done(new DoneCallback() {
				@Override
				public void onDone(Object p1) {
					callback.callback((PackageAppData)p1);
				}
			});
    }

    private PackageAppData loadAppData(String packageName) {
        InstalledAppInfo setting = VirtualCore.get().getInstalledAppInfo(packageName, 0);
        if (setting != null)
		{
            PackageAppData data = new PackageAppData(VApp.getApp(), setting);
            synchronized (packageDataMap)
			{
                packageDataMap.put(packageName, data);
            }
            return data;
        }
        return null;
    }

}
