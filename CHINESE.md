[![VA banner](https://raw.githubusercontent.com/asLody/VirtualApp/master/banner.png)](https://github.com/asLody/VirtualApp)


给`微商双开神器`的警告
----------------------
经过验证发现，`微商双开神器`将VirtualApp的`演示App`的界面改为绿色，并添加`微信支付`（售价28元），
广州市比目网络科技有限公司的这一敛财行为，严重侵害了作者的利益。请在1个月内下架你们的产品。
再次申明，VA可以使用于商业项目中，但这种赤裸裸的敛财行为，是严格禁止的。


关于
---
**VirtualApp**是一个**App虚拟引擎**的完整实现（简称`VA`）。
VirtualApp允许你在App内创建一个虚拟空间，你可以在虚拟空间内任意的`安装`、`启动`和`卸载`APK，这一切都与外部隔离，就如同一个`沙盒`。

运行在`VA`中的APK无需在外部安装，即VA支持**免安装运行APK**。


讨论技术话题
----------

1. 将你的Host和Plugins需要的**所有权限**加入到你的`AndroidManifest.xml`.

2. 前往你的Application并添加如下代码:
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

3. For **Install a virtual App**, use this function:
```java
    VirtualCore.getCore().installApp({APK PATH}, flags);
```

4. For **Launch a virtual App**, use this function:
```java
    VirtualCore.getCore().launchApp({PackageName});
```

5. For **uninstall a virtual App**, use this function:
```java
    VirtualCore.getCore().uninstallApp({PackageName});
```

6. If you need to get the `details of App`, use this function:
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
