<h1><p align="center">VA Basic Development Document</p></h1> 

This document mainly introduces 2 parts.  
The first part is the introduction of VA source code structure, this part is to allow developers to quickly understand to master the VA source code framework.    
The second part is a description of VA's basic SDK. For more development documents, see: VA Private Library Wiki.   
For more development documents, see：[VA Private Library Wiki](https://github.com/asLody/VirtualApp-Priv/wiki)  
VA Product Description：[Document](../README_eng.md)
</br>

**The following is the first part, the introduction of the VA source code structure：**

## 1. Introduction of VA source code directory ##
The following figure is the root of the VA source code：  
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/1.png)  
You can see that VA has a total of four source code directories, and each directory is described as follows：

Directory Name | Function
---- | ---
app | The directory where the VA Demo master package source code is located
app-ext | The directory where the source code of VA Demo plug-in package is located
lib | The directory where the VA library source code is located
lib-ext | The directory where the source code of VA Plugin Library is located
<br/>

## 2. Introduction of VA compilation configuration profile ##
VA compilation configuration profile isVAConfig.gradle：  
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/2_1.jpg)  

Configuration explanation：

Configuration Name | Function
---- | ---
PACKAGE_NAME | Used to configure the package name of the VA main package
EXT_PACKAGE_NAME | Used to configure the package name of the VA plug-in package
VA_MAIN_PACKAGE_32BIT | Used to configure whether the VA main package is 32-bit or 64-bit, true is 32-bit, false is 64-bit
VA_ACCESS_PERMISSION_NAME | Used to configure the permission names of the 4 major components in VA
VA_AUTHORITY_PREFIX | Used to configure the authorities of ContentProvider in the VA main packag
VA_EXT_AUTHORITY_PREFIX | Used to configure the authorities of the ContentProvider in the VA plug-in package
VA_VERSION | Used to configure the VA library version, developers generally do not need to care
VA_VERSION_CODE | Used to configure the VA library version code, developers generally do not need to care
<br/>

## 3. VA core code explanation ##
1. The code under the`com.lody.virtual.client`package runs in the VAPP Client process and is mainly used in the APP Hook part of the VA Framework to complete the HOOK processing for each service.  
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/3_1.png)  
2. The code under the`com.lody.virtual.server`package runs in the VA Server process. The code is mainly used in the APP Server part of the VA Framework to handle APP installation and other APP requests that are not handled by the Android system.  
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/3_2.png)  
3.The code under the `mirror`package is mainly used for references to the system's hidden classes, and belongs to the tool class, reducing a lot of reflection code's writing.     
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/3_3.png)  
4.The code under the `cpp`package is carried out in the VAPP Client process and is mainly used in the VA Native part. Implement IO redirection and jni function HOOK. Among them：  
	- `substrate`implements hook for arm32 and arm64  
	- `vfs.cpp`implements VA's virtual file system for controlling APP file access restrictions 
	- `syscall_hook.cpp`implements Hook for IO  
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/3_4.png)  
5.`DelegateApplicationExt.java`runs in the VA Host Plugin process，used in  the VA plug-in package,  implementing the loading and execution to the main package code.   
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/3_5.png)  

</br></br>
**The following is the second part, the introduction of using VA SDK：**

## 1. VA Project Integration ##
### Open VirtualApp-Priv project with Android Studio

Multiple modules can be seen:
* app
* app-ext
* lib
* lib-ext

Among them, **lib** and **lib-ext** belong to the VirtualApp`core library `and `extensions`，while **app** and **app-ext** belong to the`sample app`.  



### Create your own App
Create a module of type application, and add the lib module as a dependency
```gradle
implementation project(':lib')
```

### Modify VAConfig.gradle according to demand：
```gradle
ext {
    VA_MAIN_PACKAGE_32BIT = true  // The main package is 32-bit
    VA_ACCESS_PERMISSION_NAME = "io.busniess.va.permission.SAFE_ACCESS"  // The name of the permission used by the VirtualApp component
    VA_AUTHORITY_PREFIX = "io.busniess.va"  // The authority used by ContentProvider in VirtualApp cannot be duplicated with other Apps.  
    VA_EXT_AUTHORITY_PREFIX = "io.busniess.va.ext"  // The authority used by the ContentProvider in the VirtualApp extension package cannot be duplicated with other Apps.  
    // ...
}
```

### Add the required permissions in AndroidManifest.xml
```xml
<uses-permission android:name="${VA_ACCESS_PERMISSION_NAME}" />
```
Permission's name must be consistent with those declared in **VAConfig.gradle**, and adding **Placeholder** in **build.gradle** to prevent errors.  
``` gradle
android {
    // ...
    manifestPlaceholders = [
                VA_ACCESS_PERMISSION_NAME: rootProject.ext.VA_ACCESS_PERMISSION_NAME,
    ]
}
```

### Create an Application

#### Override the attachBaseContext method and add the code to bootstrap the VirtualApp：

```java
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            VirtualCore.get().startup(base, mConfig);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

```

#### Here, a configuration of VirtualApp is passed in mConfig
```java
private SettingConfig mConfig = new SettingConfig() {
        @Override
        public String getMainPackageName() {
            // Name of the main package
            return BuildConfig.APPLICATION_ID;
        }

        @Override
        public String getExtPackageName() {
            // Name of extension package
            return BuildConfig.EXT_PACKAGE_NAME;
        }

        @Override
        public boolean isEnableIORedirect() {
            // Whether to enable IO redirection, it is recommended to enable
            return true;
        }

        @Override
        public Intent onHandleLauncherIntent(Intent originIntent) {
            // Back to the desktop of the Intent interception operation. Here change the action that back to the desktop to return to BackHomeActivity page of the main package.    
            Intent intent = new Intent();
            ComponentName component = new ComponentName(getMainPackageName(), BackHomeActivity.class.getName());
            intent.setComponent(component);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return intent;
        }

        @Override
        public boolean isUseRealDataDir(String packageName) {
            // The data path simulates the real path format and requires IO redirection to be enabled. Some of the hardening will check the path format.    
            return false;
        }

        @Override
        public boolean isOutsidePackage(String packageName) {
            // Whether is an external App. Set the external App to be visible to the internal App.  
            return false;
        }

        @Override
        public boolean isAllowCreateShortcut() {
            // Whether allow to create desktop shortcut icons. It is recommended to turn off (false) and implement desktop shortcuts by yourself.  
            return false;
        }

        @Override
        public boolean isHostIntent(Intent intent) {
            // Whether the Intent is handled by VirtualApp.
            return intent.getData() != null && "market".equals(intent.getData().getScheme());
        }

        @Override
        public boolean isUseRealApkPath(String packageName) {
            // The installation apk path simulates the real path and requires IO redirection to be enabled. Some hardening will check the path format.    
            return false;
        }

        @Override
        public boolean isEnableVirtualSdcardAndroidData() {
            // Enable redirection of `Android/data` directory under external storage.    
            // Require redirection support.  
            // Must be enabled after Android 11！！  
            return BuildCompat.isR();
        }

        @Override
        public String getVirtualSdcardAndroidDataName() {
            // Set the redirect path for  `Android/data` directory under external storage.  
            // /sdcard/Android/data/com.example.test/ ==>> /sdcard/{VirtualSdcardAndroidDataName}/{user_id}/Android/data/com.example.test/
            return "Android_va";
        }

        @Override
        public FakeWifiStatus getFakeWifiStatus() {
            // Modify the wifi information. null is not modified.  
            return null;
        }

        @Override
        public boolean isHideForegroundNotification() {
            // Hide foreground messages, not recommended to hide.  
            return false;
        }

        @Override
        public boolean isOutsideAction(String action) {
            // Action event response of external Intent.  
            return MediaStore.ACTION_IMAGE_CAPTURE.equals(action)
                || MediaStore.ACTION_VIDEO_CAPTURE.equals(action)
                || Intent.ACTION_PICK.equals(action);
        }

        @Override
        public boolean isDisableDrawOverlays(String packageName) {
            // Disable top-level overlay (floating window) for VAPP.  
            return false;
        }
    };
```

### Override onCreate and add the code of initialize the VirtualApp：
```java
    @Override
    public void onCreate() {
        super.onCreate();
        VirtualCore virtualCore = VirtualCore.get();
        virtualCore.initialize(new VirtualCore.VirtualInitializer() {
            @Override
            public void onMainProcess() {
                // Main process callback
            }

            @Override
            public void onVirtualProcess() {
                // Virtual App process callback
            }

            @Override
            public void onServerProcess() {
                // Server-side process callback
            }

            @Override
            public void onChildProcess() {
                // Other sub-process callback
            }
        });
    }

```

Since VirtualApp will start multiple processes, Application will enter N times, and different processes will go to different callbacks of VirtualInitializer, where additional initialization code can be added depending on the process type.  

## 2. Install the APP ##
## API:
```java
VirtualCore.java

 public VAppInstallerResult installPackage(Uri uri, VAppInstallerParams params);
```
## What is the parameter Uri?
Uri determines the source of **the apk that need to be installed**,and currently supports both package and file protocols.    
### Package Uri Example:
```java
Uri packageUri = Uri.parse("package:com.hello.world");
```
### File Uri Example:
```java
File apkFile = new File("/sdcard/test.apk"); 
Uri packageUri = Uri.fromFile(apkFile);
```

## What is the difference between the two types of Uri installation apps?
**package agreement**  To install the app, you only need to pass in the package name, not the specific APK path, so the app installed with this protocol **is equivalent to double space**.  

The APP is automatically upgraded as external versions are upgraded and uninstalled as external versions are uninstalled. `dynamic` in `PackageSetting` is `true`。

**file agreement** It is an internal installation, apk is copied inside the container, completely independent from the external version. `dynamic` in `PackageSetting` is `false`。

## Installation Parameters VAppInstallerParams

### Installation Flags installFlags

FLAG | Instruction
--- | ---
FLAG_INSTALL_OVERRIDE_NO_CHECK | Allow overlay installation
FLAG_INSTALL_OVERRIDE_FORBIDDEN | Prohibit overlay installation
FLAG_INSTALL_OVERRIDE_DONT_KILL_APP | Overwrite installation, and not kill the already launched APP

### Installation Mode mode

FLAG | Instruction
--- | ---
MODE_FULL_INSTALL | Complete installation
MODE_INHERIT_EXISTING | The installed installation mode of the installed. Reserve.  

Reserve parameters, not used for now. Currently the same no matter which one is set.  

### cpuAbiOverride

Specify the abi of the App. You can force the App to run under the specified abi in special need. If you don't specify, the default abi is determined by `system rules`.  
Optional parameters：
* armeabi
* armeabi-v7a
* arm64-v8a

### Double space App example code：
```java
VAppInstallerParams params = new VAppInstallerParams(VAppInstallerParams.FLAG_INSTALL_OVERRIDE_NO_CHECK);
VAppInstallerResult result = VirtualCore.get().installPackage(Uri.parse("package:com.tencent.mobileqq"), params);
if (result.status == VAppInstallerResult.STATUS_SUCCESS) {
    Log.e("test", "install apk success.");
}
```

### Install apk from sd card example code：
```java
VAppInstallerParams params = new VAppInstallerParams(VAppInstallerParams.FLAG_INSTALL_OVERRIDE_NO_CHECK);
VAppInstallerResult result = VirtualCore.get().installPackage(Uri.fromFile(new File("/sdcard/test.apk")), params);
if (result.status == VAppInstallerResult.STATUS_SUCCESS) {
    Log.e("test", "install apk success.");
}
```

### Install Split apk
Just install the base package firstly, and then install all the split packages.  
```java
File dir = new File("/sdcard/YouTube_XAPK_Unzip/");
VAppInstallerParams params = new VAppInstallerParams(VAppInstallerParams.FLAG_INSTALL_OVERRIDE_NO_CHECK);
VAppInstallerResult result = VirtualCore.get().installPackage(
        Uri.fromFile(new File(dir,"com.google.android.youtube.apk")), params);
for (File file : dir.listFiles()) {
    String name = file.getName();
    if (name.startsWith("config.") && name.endsWith(".apk")) {
        result = VirtualCore.get().installPackage(
            Uri.fromFile(file), params);
    }
}

```




## 3. Launch and manage Application ##
# Launch App

```java
// class VActivityManager
public boolean launchApp(final int userId, String packageName)
````
Example code：
```java
VActivityManager.get().launchApp(0, "com.tencent.mobileqq");
```

# Kill App
```java
// class VActivityManager
public void killAppByPkg(String pkg, int userId)
public void killAllApps()
```
Example code：
```java
// Kill the QQ program process with userid 0
VActivityManager.get().killAppByPkg("com.tencent.mobileqq", 0);

```

```java
// Kill all App processes
VActivityManager.get().killAllApps();
```

# Uninstall App
```java
// class VirtualCore
public boolean uninstallPackageAsUser(String pkgName, int userId)
public boolean uninstallPackage(String pkgName)
```
Example code：
```java
// Uninstall the QQ program with userid 0
VirtualCore.get().uninstallPackageAsUser("com.tencent.mobileqq", 0);
// Uninstall the QQ programs installed under all user
VirtualCore.get().uninstallPackage("com.tencent.mobileqq");
```

# Check the installed Apps
```java
// class VirtualCore
public List<InstalledAppInfo> getInstalledApps(int flags)
```

## 4. Java Hook Usage ##
VirtualApp implements a set of Xposed interface. Users who can use Xposed can do things that originally need the system built-in Xposed to do.  
However, users also need to understand that the scope of Xposed in VA is within the VA app. Cannot overstep the authority to control system or other external apps.  


VA provides a callback interface of App creation and launch `com.lody.virtual.client.core.AppCallback`, the interfaces are as follows:
```java
public interface AppCallback {
    void beforeStartApplication(String packageName, String processName, Context context);

    void beforeApplicationCreate(String packageName, String processName, Application application);

    void afterApplicationCreate(String packageName, String processName, Application application);
}
```

> Interface Instruction:

Name | Instruction
---- | ---
beforeStartApplication | Before APP launch, after creation
beforeApplicationCreate | Before the APP is created, application has already prepared, Application.OnCreate is not executed.
afterApplicationCreate | After the APP is created, Application.OnCreate is executed.

<br/>

>Parameter Instruction:

Name | Instruction
---- | ---
packageName | Name of VAPP
processName | Process name of VAPP
context | Application context of VAPP
application | Application of VAPP

<br/>

> Note: APP creation means that`Application`is created.

The interface is there, and the next step is how to use it. View[`VirualApp Process Instruction`](VirualApp Process Instruction.md), we kan see  
We just need to put in the`VAPP process`callback(`onVirtualProcess`) set App callback `AppCallback` and then achieve the purpose.

> Host Application code, please refer [io/busniess/va/App.java](https://github.com/asLody/VirtualApp-Priv/blob/v2.1/VirtualApp/app/src/main/java/io/busniess/va/App.java)

```java
@Override
    public void onCreate() {
        super.onCreate();
        VirtualCore virtualCore = VirtualCore.get();
        virtualCore.initialize(new VirtualCore.VirtualInitializer() {
            @Override
            public void onVirtualProcess() {
                // Set VAPP launch callback
                virtualCore.setAppCallback(new MyComponentDelegate());
            }
        });
    }
```

> [MyComponentDelegate](https://github.com/asLody/VirtualApp-Priv/blob/v2.1/VirtualApp/app/src/main/java/io/busniess/va/delegate/MyComponentDelegate.java)Class Code

```java
public class MyComponentDelegate implements AppCallback {

    @Override
    public void beforeStartApplication(String packageName, String processName, Context context) {
    }

    @Override
    public void beforeApplicationCreate(String packageName, String processName, Application application) {

        XposedHelpers.findAndHookMethod("android.app.ContextImpl", ClassLoader.getSystemClassLoader(), "getOpPackageName", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                VLog.printStackTrace("getOpPackageName");
                param.setResult(VirtualCore.get().getHostPkg());
            }
        });
    }

    @Override
    public void afterApplicationCreate(String packageName, String processName, Application application) {
    }
}
```

In the above example, a use case for Xposed has been added. The entry point of Xposed is an example of `IXposedHookLoadPackage`, it provides an interface of `void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam)`. There is one parameter of `XC_LoadPackage.LoadPackageParam`. Here we can't use it exactly one by one, but it's enough. `loadPackageParam.classsload`can use  `context.getClassLoader()` or `application.getClassLoader()` both are ok. Afterwards `XposedHelpers`, the same way how to use`XposedBridge`, is also used here.  



## 5. Native Hook Usage ##
For ARM 32 and ARM 64 Hooks, only the header files ```CydiaSubstrate.h``` need to be introduced, i.e. the Hook API:  
```MSHookFunction(Type_ *symbol, Type_ *replace, Type_ **result)```  
>Parameter Instruction:

Name | Instruction
---- | ---
symbol | Address to Hook  
replace | Your custom Hook function
result | Backups of hooked function
<br/>
Refer ```syscall_hook.cpp``` code
```cpp
auto is_accessible_str = "__dl__ZN19android_namespace_t13is_accessibleERKNSt3__112basic_stringIcNS0_11char_traitsIcEENS0_9allocatorIcEEEE";
void *is_accessible_addr = getSym(linker_path, is_accessible_str);
if (is_accessible_addr) {
    MSHookFunction(is_accessible_addr, (void *) new_is_accessible,(void **)     &orig_is_accessible);
}
```

Within `MSHookFunction`, it automatically determines whether the current is ARM32 or ARM64：


```cpp
_extern void MSHookFunction(void *symbol, void *replace, void **result) {
    if (*result != nullptr) {
        return;
    }
    // ALOGE("[MSHookFunction] symbol(%p) replace(%p) result(%p)", symbol, replace, *result);
#ifdef __aarch64__
    A64HookFunction(symbol, replace, result);
#else
    SubstrateHookFunction(NULL, symbol, replace, result);
#endif
}
```


</br>
</br>

[Additional development guidance can be found on the VA Private Library Wiki](https://github.com/asLody/VirtualApp-Priv/wiki)

</br>
</br>
