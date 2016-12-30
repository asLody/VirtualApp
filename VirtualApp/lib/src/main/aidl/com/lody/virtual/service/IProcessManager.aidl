// IProcessManager.aidl
package com.lody.virtual.service;

import com.lody.virtual.helper.proto.VComponentInfo;
import com.lody.virtual.service.interfaces.IProcessObserver;

interface IProcessManager {

    void onAppProcessCreate(in IBinder appThread);

    void onEnterApp(String pkg);

    void onEnterAppProcessName(String pluginProcessName);

    void installComponent(in VComponentInfo componentInfo);

    boolean isAppProcess(String processName);

    boolean isAppPid(int pid);

    String getAppProcessName(int pid);

    List<String> getProcessPkgList(int pid);

    void killAllApps();

    void killAppByPkg(String pkg);

    void killApplicationProcess(String procName, int uid);

    void dump();

    void registerProcessObserver(in IProcessObserver observer);

    void unregisterProcessObserver(in IProcessObserver observer);


}
