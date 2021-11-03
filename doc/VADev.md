<h1><p align="center">VA基础开发文档</p></h1> 

本文档主要介绍2部分。  
第一部分是VA的源码结构介绍，这部分是为了让开发者能快速了解掌握VA源码框架。  
第二部分是VA的基础SDK使用说明。 
其他更多的开发文档见：[VA私有库Wiki](https://github.com/asLody/VirtualApp-Priv/wiki)  
VA产品说明：[文档](../README.md)
</br>

**下面开始第一部分，VA源码结构介绍：**

## 1. VA源码目录介绍 ##
下图是VA源码根目录：  
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/1.png)  
可以看到VA一共有4个源码目录，各个目录介绍如下：

目录名称 | 作用
---- | ---
app | VA Demo主包源码所在目录
app-ext | VA Demo插件包源码所在目录
lib | VA库源码所在目录
lib-ext | VA插件库源码所在目录
<br/>

## 2. VA编译配置文件介绍 ##
VA的编译配置文件是VAConfig.gradle：  
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/2_1.jpg)  

配置解释：

配置名称 | 作用
---- | ---
PACKAGE_NAME | 用于配置VA主包的包名
EXT_PACKAGE_NAME | 用于配置VA插件包的包名
VA_MAIN_PACKAGE_32BIT | 用于配置VA主包是32位还是64位，true为32位，false为64位
VA_ACCESS_PERMISSION_NAME | 用于配置VA中4大组建的权限名称
VA_AUTHORITY_PREFIX | 用于配置VA主包中ContentProvider的authorities
VA_EXT_AUTHORITY_PREFIX | 用于配置VA插件包中ContentProvider的authorities
VA_VERSION | 用于配置VA库版本，开发者一般不需要关心
VA_VERSION_CODE | 用于配置VA库版本代码，开发者一般不需要关心
<br/>

## 3. VA核心代码解释 ##
1. `com.lody.virtual.client`包下的代码运行在VAPP Client进程中，主要用于VA Framework中的APP Hook部分，完成对各个Service的HOOK处理  
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/3_1.png)  
2. `com.lody.virtual.server`包下的代码运行在VA Server进程中，代码主要用于VA Framework中的APP Server部分，实现处理APP安装以及其他不给Android系统处理的APP请求  
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/3_2.png)
3. `mirror`包下的代码主要用于对系统隐藏类的引用，属于工具类，减少大量反射代码的编写  
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/3_3.png)
4. `cpp`包下的代码进行在VAPP Client进程中，主要用于VA Native部分，实现IO重定向和jni函数HOOK。其中：  
	- `substrate`中实现了针对arm32和arm64的hook  
	- `vfs.cpp`中实现了VA的虚拟文件系统，用于控制APP文件访问限制  
	- `syscall_hook.cpp`中实现了对IO的Hook  
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/3_4.png)  
5. `DelegateApplicationExt.java`运行在VA Host Plugin进程中，用于VA插件包，实现了对主包代码的加载执行  
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/3_5.png)  

</br></br>
**下面开始第二部分，VA SDK使用介绍：**

## 1. VA工程接入 ##
### 用Android Studio打开VirtualApp-Priv项目

可见多个模块:
* app
* app-ext
* lib
* lib-ext

其中**lib**和**lib-ext**属于VirtualApp`核心库`以及`扩展库`，**app**和**app-ext**则属于`示例app`。



### 创建自己的App
新建一个application类型的module，并添加lib模块为依赖
```gradle
implementation project(':lib')
```

### 根据需求修改VAConfig.gradle：
```gradle
ext {
    VA_MAIN_PACKAGE_32BIT = true  // 主包为32位
    VA_ACCESS_PERMISSION_NAME = "io.busniess.va.permission.SAFE_ACCESS"  // VirtualApp组件用到的权限名称
    VA_AUTHORITY_PREFIX = "io.busniess.va"  // VirtualApp中ContentProvider用到的authority，不能与其他app重复
    VA_EXT_AUTHORITY_PREFIX = "io.busniess.va.ext"  // VirtualApp扩展包中ContentProvider用到的authority，不能与其他app重复
    // ...
}
```

### 在AndroidManifest.xml添加所需的权限
```xml
<uses-permission android:name="${VA_ACCESS_PERMISSION_NAME}" />
```
权限名称必须与**VAConfig.gradle**中所声明的保持一致，可以在**build.gradle**中添加**Placeholder**来防止出错。

``` gradle
android {
    // ...
    manifestPlaceholders = [
                VA_ACCESS_PERMISSION_NAME: rootProject.ext.VA_ACCESS_PERMISSION_NAME,
    ]
}
```

### 创建一个Application

#### 复写attachBaseContext方法，添加引导VirtualApp的代码：

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

#### 这里传入了一个VirtualApp的一个配置 mConfig
```java
private SettingConfig mConfig = new SettingConfig() {
        @Override
        public String getMainPackageName() {
            // 主包的包名
            return BuildConfig.APPLICATION_ID;
        }

        @Override
        public String getExtPackageName() {
            // 扩展包包名
            return BuildConfig.EXT_PACKAGE_NAME;
        }

        @Override
        public boolean isEnableIORedirect() {
            // 是否启用IO重定向，建议开启
            return true;
        }

        @Override
        public Intent onHandleLauncherIntent(Intent originIntent) {
            // 回到桌面的 Intent 拦截操作，这里把回到桌面的动作改成回到主包的BackHomeActivity页面
            Intent intent = new Intent();
            ComponentName component = new ComponentName(getMainPackageName(), BackHomeActivity.class.getName());
            intent.setComponent(component);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return intent;
        }

        @Override
        public boolean isUseRealDataDir(String packageName) {
            // data路径模拟真实路径格式，需要启用IO重定向。部分加固会校该验路径格式
            return false;
        }

        @Override
        public boolean isOutsidePackage(String packageName) {
            // 是否是外部app。 设置外部 app 对内部app看见
            return false;
        }

        @Override
        public boolean isAllowCreateShortcut() {
            // 是否允许创建桌面快捷图标。建议关闭（false），自己实现桌面快捷方式
            return false;
        }

        @Override
        public boolean isHostIntent(Intent intent) {
            // 是否由VirtualApp处理的Intent
            return intent.getData() != null && "market".equals(intent.getData().getScheme());
        }

        @Override
        public boolean isUseRealApkPath(String packageName) {
            // 安装apk路径模拟真实路径，需要启用IO重定向。部分加固会校验该路径格式
            return false;
        }

        @Override
        public boolean isEnableVirtualSdcardAndroidData() {
            // 启用外置存储下的 `Android/data` 目录的重定向
            // 需要重定向支持
            // Android 11 之后必须启用！！
            return BuildCompat.isR();
        }

        @Override
        public String getVirtualSdcardAndroidDataName() {
            // 设置外置存储下的 `Android/data` 目录的重定向路径
            // /sdcard/Android/data/com.example.test/ ==>> /sdcard/{VirtualSdcardAndroidDataName}/{user_id}/Android/data/com.example.test/
            return "Android_va";
        }

        @Override
        public FakeWifiStatus getFakeWifiStatus() {
            // 修改wifi信息。 null 则不修改
            return null;
        }

        @Override
        public boolean isHideForegroundNotification() {
            // 隐藏前台消息，不建议隐藏
            return false;
        }

        @Override
        public boolean isOutsideAction(String action) {
            // 外部 Intent 的 action 事件响应
            return MediaStore.ACTION_IMAGE_CAPTURE.equals(action)
                || MediaStore.ACTION_VIDEO_CAPTURE.equals(action)
                || Intent.ACTION_PICK.equals(action);
        }

        @Override
        public boolean isDisableDrawOverlays(String packageName) {
            // 禁用 VAPP 的顶层覆盖（浮窗）。
            return false;
        }
    };
```

### 复写onCreate，添加初始化VirtualApp的代码：
```java
    @Override
    public void onCreate() {
        super.onCreate();
        VirtualCore virtualCore = VirtualCore.get();
        virtualCore.initialize(new VirtualCore.VirtualInitializer() {
            @Override
            public void onMainProcess() {
                // 主进程回调
            }

            @Override
            public void onVirtualProcess() {
                // 虚拟App进程回调
            }

            @Override
            public void onServerProcess() {
                // 服务端进程回调
            }

            @Override
            public void onChildProcess() {
                // 其他子进程回调
            }
        });
    }

```

由于VirtualApp会启动多个进程，所以Application会进入N次，不同的进程会走到VirtualInitializer不同的回调，可以在这里根据进程类型添加额外的初始化代码。

## 2. 安装APP ##
## API:
```java
VirtualCore.java

 public VAppInstallerResult installPackage(Uri uri, VAppInstallerParams params);
```
## 参数Uri是什么?
Uri决定了**需要安装的apk**的来源，目前支持 package 和 file 协议。
### Package Uri 示例:
```java
Uri packageUri = Uri.parse("package:com.hello.world");
```
### File Uri 示例:
```java
File apkFile = new File("/sdcard/test.apk"); 
Uri packageUri = Uri.fromFile(apkFile);
```

## 两种Uri安装app有何区别?
**package协议** 安装app，只需要传入包名，不需要具体的APK路径，所以以这种协议安装的app，**相当于双开**。

app会随外部版本的升级而自动升级，随外部版本的卸载而自动卸载。`PackageSetting` 中的 `dynamic` 为 `true`。

**file协议** 则是内部安装，apk会被复制到容器内部，与外部版本完全独立. `PackageSetting` 中的 `dynamic` 为 `false`。

## 安装参数 VAppInstallerParams

### 安装标志 installFlags

FLAG | 说明
--- | ---
FLAG_INSTALL_OVERRIDE_NO_CHECK | 允许覆盖安装
FLAG_INSTALL_OVERRIDE_FORBIDDEN | 禁止覆盖安装
FLAG_INSTALL_OVERRIDE_DONT_KILL_APP | 覆盖安装不kill已经启动的APP

### 安装模式 mode

FLAG | 说明
--- | ---
MODE_FULL_INSTALL | 完整安装
MODE_INHERIT_EXISTING | 已安装的的安装模式。预留

预留参数，暂时未使用。目前不管设置哪种都一样。

### cpuAbiOverride

指定app的abi。特殊需求下，可以强制指定app在指定abi下运行。不指定的情况下默认根据`系统规则`来决定运行的abi。

可选参数：
* armeabi
* armeabi-v7a
* arm64-v8a

### 双开app实例代码：
```java
VAppInstallerParams params = new VAppInstallerParams(VAppInstallerParams.FLAG_INSTALL_OVERRIDE_NO_CHECK);
VAppInstallerResult result = VirtualCore.get().installPackage(Uri.parse("package:com.tencent.mobileqq"), params);
if (result.status == VAppInstallerResult.STATUS_SUCCESS) {
    Log.e("test", "install apk success.");
}
```

### 从sd卡安装apk实例代码：
```java
VAppInstallerParams params = new VAppInstallerParams(VAppInstallerParams.FLAG_INSTALL_OVERRIDE_NO_CHECK);
VAppInstallerResult result = VirtualCore.get().installPackage(Uri.fromFile(new File("/sdcard/test.apk")), params);
if (result.status == VAppInstallerResult.STATUS_SUCCESS) {
    Log.e("test", "install apk success.");
}
```

### 安装Split apk
先安装base包，然后再安装所有split包即可。
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




## 3. 启动及管理Application ##
# 启动App

```java
// class VActivityManager
public boolean launchApp(final int userId, String packageName)
````
实例代码：
```java
VActivityManager.get().launchApp(0, "com.tencent.mobileqq");
```

# 杀死App
```java
// class VActivityManager
public void killAppByPkg(String pkg, int userId)
public void killAllApps()
```
实例代码：
```java
// 杀死userid为0的QQ程序进程
VActivityManager.get().killAppByPkg("com.tencent.mobileqq", 0);

```

```java
// 杀死所有App进程
VActivityManager.get().killAllApps();
```

# 卸载App
```java
// class VirtualCore
public boolean uninstallPackageAsUser(String pkgName, int userId)
public boolean uninstallPackage(String pkgName)
```
实例代码：
```java
// 卸载userid为0的QQ程序
VirtualCore.get().uninstallPackageAsUser("com.tencent.mobileqq", 0);
// 卸载所有user下安装的QQ程序
VirtualCore.get().uninstallPackage("com.tencent.mobileqq");
```

# 查询已安装的App
```java
// class VirtualCore
public List<InstalledAppInfo> getInstalledApps(int flags)
```

## 4. Java Hook使用 ##
VirtualApp中实现了一套Xposed接口,用户只要会使用Xposed就可以做到原本需要系统内置Xposed才能做到的事情.
但是用户也需要明白,VA中Xposed的作用域是VA这个APP中的,不能越权控制系统或其他外部App.


VA中提供了一个App创建启动的回调接口`com.lody.virtual.client.core.AppCallback`,接口如下:
```java
public interface AppCallback {
    void beforeStartApplication(String packageName, String processName, Context context);

    void beforeApplicationCreate(String packageName, String processName, Application application);

    void afterApplicationCreate(String packageName, String processName, Application application);
}
```

> 接口说明:

名称 | 说明
---- | ---
beforeStartApplication | APP启动之前,创建之后
beforeApplicationCreate | APP被创建之前,Application已经准备完毕,Application.OnCreate未执行
afterApplicationCreate | APP被创建之后,Application.OnCreate已被执行

<br/>

>参数说明:

名称 | 说明
---- | ---
packageName | VAPP的包名
processName | VAPP的进程名
context | VAPP的Application context
application | VAPP的Application

<br/>

> 注: APP的创建指的是`Application`被创建.

接口有了,接下来就是怎么使用了.查看[`VirualApp进程说明`](VirualApp进程说明.md),可以知道,
我们只需要在`VAPP进程`回调里(`onVirtualProcess`) 设置App回调 `AppCallback` 就可以达到目的.

> 宿主Application代码,参考[io/busniess/va/App.java](https://github.com/asLody/VirtualApp-Priv/blob/v2.1/VirtualApp/app/src/main/java/io/busniess/va/App.java)

```java
@Override
    public void onCreate() {
        super.onCreate();
        VirtualCore virtualCore = VirtualCore.get();
        virtualCore.initialize(new VirtualCore.VirtualInitializer() {
            @Override
            public void onVirtualProcess() {
                // 设置VAPP启动回调
                virtualCore.setAppCallback(new MyComponentDelegate());
            }
        });
    }
```

> [MyComponentDelegate](https://github.com/asLody/VirtualApp-Priv/blob/v2.1/VirtualApp/app/src/main/java/io/busniess/va/delegate/MyComponentDelegate.java)类代码

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

上面示例中,已经添加了一个Xposed的使用案例.Xposed的入口是一个`IXposedHookLoadPackage`的实例,他提供了一个`void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam)`的接口,有一个`XC_LoadPackage.LoadPackageParam`的参数.这里我们虽然不能完全一一对用,但是也完全够用了.`loadPackageParam.classsload`可以用`context.getClassLoader()`或者`application.getClassLoader()`都是可以的.后续`XposedHelpers`,`XposedBridge`原来怎么用,这里也一样使用.



## 5. Native Hook使用 ##
对于ARM 32和ARM 64的Hook，只需要引入头文件```CydiaSubstrate.h```即可,Hook API:  
```MSHookFunction(Type_ *symbol, Type_ *replace, Type_ **result)```  
>参数说明:

名称 | 说明
---- | ---
symbol | 要Hook的地址  
replace | 你自定义的hook函数
result | 被hook函数的备份
<br/>
参考```syscall_hook.cpp```代码

```cpp
auto is_accessible_str = "__dl__ZN19android_namespace_t13is_accessibleERKNSt3__112basic_stringIcNS0_11char_traitsIcEENS0_9allocatorIcEEEE";
void *is_accessible_addr = getSym(linker_path, is_accessible_str);
if (is_accessible_addr) {
    MSHookFunction(is_accessible_addr, (void *) new_is_accessible,(void **)     &orig_is_accessible);
}
```

在`MSHookFunction`内部会自动判断当前是ARM32还是ARM64：


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

[其他更多的开发指导请见VA私有库Wiki](https://github.com/asLody/VirtualApp-Priv/wiki)

</br>
</br>

