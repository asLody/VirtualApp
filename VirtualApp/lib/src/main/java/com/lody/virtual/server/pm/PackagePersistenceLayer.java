package com.lody.virtual.server.pm;

import android.content.pm.PackageParser;
import android.os.Parcel;

import com.lody.virtual.helper.PersistenceLayer;
import com.lody.virtual.os.VEnvironment;

import java.util.Arrays;

/**
 * @author Lody
 */

class PackagePersistenceLayer extends PersistenceLayer {

    private static final char[] MAGIC = {'v', 'p', 'k', 'g'};
    private static final int CURRENT_VERSION = 2;

    private VAppManagerService mService;

    PackagePersistenceLayer(VAppManagerService service) {
        super(VEnvironment.getPackageListFile());
        mService = service;
    }

    @Override
    public int getCurrentVersion() {
        return CURRENT_VERSION;
    }

    @Override
    public void writeMagic(Parcel p) {
        p.writeCharArray(MAGIC);
    }

    @Override
    public boolean verifyMagic(Parcel p) {
        char[] magic = p.createCharArray();
        return Arrays.equals(magic, MAGIC);
    }


    @Override
    public void writePersistenceData(Parcel p) {
        synchronized (PackageCache.PACKAGE_CACHE) {
            p.writeInt(PackageCache.PACKAGE_CACHE.size());
            for (PackageParser.Package pkg : PackageCache.PACKAGE_CACHE.values()) {
                PackageSetting setting = (PackageSetting) pkg.mExtras;
                setting.writeToParcel(p, 0);
            }
        }
    }

    @Override
    public void readPersistenceData(Parcel p) {
        int count = p.readInt();
        while (count-- > 0) {
            PackageSetting setting = new PackageSetting(p);
            mService.loadPackage(setting);
        }
    }

    @Override
    public boolean onVersionConflict(int fileVersion, int currentVersion) {
        return false;
    }

    @Override
    public void onPersistenceFileDamage() {
        getPersistenceFile().delete();
        VAppManagerService.get().restoreFactoryState();
    }
}
