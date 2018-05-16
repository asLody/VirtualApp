[![Build Status](https://travis-ci.org/android-hacker/VirtualXposed.svg?branch=exposed)](https://travis-ci.org/android-hacker/VirtualXposed)

简介
-----
本项目是在[VirtualApp](https://github.com/asLody/VirtualApp) 的基础上再次开发的，适配了一些在使用过程中出现的bug。

警告
-------
本项目使用的 VirtualApp 不允许用于商业用途，如果有这个需求，请联系 Lody (zl@aluohe.com)。

使用
----------

[猛戳这里](CHINESE.md "中文")

使用说明
----------

**前往你的Application并添加如下代码:**

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

**安装App:**

```java
    VirtualCore.getCore().installApp({APK PATH}, flags);
```

**启动App:**

```java
    VirtualCore.getCore().launchApp({PackageName});
```

**移除App:**

```java
    VirtualCore.getCore().uninstallApp({PackageName});
```

**该App的基本信息:**

```java
    VirtualCore.getCore().findApp({PackageName});
```
