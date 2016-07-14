[![VA banner](https://raw.githubusercontent.com/asLody/VirtualApp/master/banner.png)](https://github.com/asLody/VirtualApp)

[中文](CHINESE.md "中文")

About
-----
Likes `LBE Parallel Space`, **VirtualApp** is an open platform for Android that allows you to create a `Virtual Space`,
you can install and run apk inside. Beyond that, VirtualApp is also a `Plugin Framework`,
the plugins running on VirtualApp does not require any constraints.
VirtualApp does **not** require root, it is running on the `local process`.


Background
----------

VirtualApp was born in early 2015, Originally, it is just a simple plugin framework, 
But as time goes on,
the compatibility of it is getting better and better.
in the end, it evolved into a `Virtual Container`.


Get started
-----------

1. VirtualApp use the `@hide API`, 
so you must use our `android.jar` replace the old one **(Android-SDK/platforms/android-23/{android.jar})**. 

2. Include the following attributes in your `AndroidManifest.xml`:
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

3. Add **all permissions** your host and your plugins need to use.

4. Goto your Application and insert the following code:
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

    @Override
    public void onCreate() {
        super.onCreate();
        VirtualCore.getCore().handleApplication(this);
        if (!VirtualCore.getCore().isVAppProcess()) {
            // Do some thing...
        }
    }
```

5. For **Install a virtual App**, use this function:
```java
    VirtualCore.getCore().installApp({APK PATH}, flags);
    
    //flags values
    
    public interface InstallStrategy {
	    int TERMINATE_IF_EXIST = 10;
	    int UPDATE_IF_EXIST = 11;
	    int COMPARE_VERSION = 12;
	    int IGNORE_NEW_VERSION = 13;
    }
    
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

Documentation
-------------

VirtualApp currently has **no documentation**, If you are interested in VirtualApp,please send email to me.

License
-------
LGPL 3.0

About Author
------------

    Lody (imlody@foxmail.com)
