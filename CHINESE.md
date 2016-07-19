[![VA banner](https://raw.githubusercontent.com/asLody/VirtualApp/master/banner.png)](https://github.com/asLody/VirtualApp)

关于
---
**VirtualApp**是一个**App虚拟引擎**的完整实现（简称`VA`）。
VirtualApp允许你在App内创建一个虚拟空间，你可以在虚拟空间内任意的`安装`、`启动`和`卸载`APK，这一切都与外部隔离，就如同一个`沙盒`。

运行在`VA`中的APK无需在外部安装，即VA支持**免安装运行APK**。

在项目中，你可以通过引入VA来实现`插件化`和`双开`，

在安全领域，也可以通过VA来实现`动态的App行为分析`，

在测试领域，你可以通过VA来实现`高度可定制的App单元测试`.

背景
---

VirtualApp诞生于2015年，经过一年的锤炼，才有了现在的`性能`和`兼容性`。

讨论技术话题
----------

QQ Group: **553070909**

快速开始
------

**注意：** 编译和运行项目请关闭你的`Instant run`.

1. VirtualApp 使用了 `@hide API`, 
因此你必须使用我们的 `android.jar` 来替换你已有的那个 **(Android-SDK/platforms/android-23/{android.jar})**. 

2. 在你的 `AndroidManifest.xml` 添加如下代码:
```xml
    <permission
        android:name="com.lody.virtual.permission.VIRTUAL_BROADCAST"
        android:protectionLevel="signature" />
    <uses-permission android:name="com.lody.virtual.permission.VIRTUAL_BROADCAST" />
    <service android:name="com.lody.virtual.client.stub.KeepService" android:process=":x"/>
    <provider
            android:process=":x"
            android:authorities="virtual.service.BinderProvider"
            android:name="com.lody.virtual.service.BinderProvider"
            android:exported="false" />
    <activity
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:name="com.lody.virtual.client.stub.ShortcutHandleActivity" android:exported="true"/>        
    <activity
            android:configChanges="mcc|mnc|locale|touchscreen|keyboard|keyboardHidden|navigation|orientation|screenLayout|uiMode|screenSize|smallestScreenSize|fontScale"
            android:name="com.lody.virtual.client.stub.StubActivity$C0" android:process=":p0" >
            <meta-data android:name="X-Identity" android:value="Stub-User"/>
    </activity>
    <provider
            android:process=":p0"
            android:authorities="virtual.client.stub.StubContentProvider0"
            android:name="com.lody.virtual.client.stub.StubContentProvider$C0"
            android:exported="false">
            <meta-data android:name="X-Identity" android:value="Stub-User"/>
    </provider>
    <!--and so on-->
```
3. 将你的Host和Plugins需要的**所有权限**加入到你的`AndroidManifest.xml`.

4. 前往你的Application并添加如下代码:
```java
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            VirtualCore.getCore().startup(base);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
```

5. For **Install a virtual App**, use this function:
```java
    VirtualCore.getCore().installApp({APK PATH}, flags);
```

6. For **Launch a virtual App**, use this function:
```java
    VirtualCore.getCore().launchApp({PackageName});
```

7. For **uninstall a virtual App**, use this function:
```java
    VirtualCore.getCore().uninstallApp({PackageName});
```

8. If you need to get the `details of App`, use this function:
```java
    VirtualCore.getCore().findApp({PackageName});
```


文档
-------------

VirtualApp 目前暂时**没有文档**，Please read the fucking source code。

License
-------
LGPL 3.0

关于Author
------------

    Lody (imlody@foxmail.com)
