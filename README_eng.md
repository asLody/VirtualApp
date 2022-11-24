
[中文文档](README.md "中文")

<h1><p align="center">VA Product description & Development guidance</p></h1> 

## What is VA? ##
VirtualAPP (abbreviation: VA) is a sandbox product running on Android system, which can be understood as a lightweight "Android virtual machine". Its product form is a highly extensible, customizable, integrated SDK that allows you to develop a variety of seemingly impossible projects based on or using VA. Now, VA is widely used in many technology fields as following: mini game collection, blockchain, cloud control, silent hot fix and so on. On the one hand, you can realize cloud control mobile office security and achieve military and government data isolation with VA. On the other hand, you can implement script automation, device-info-mock, and plug-in development. Meanwhile, you can realize multi space and games booster. You can also rent the mobile game account and use the mobile controller without activation by VA. <br> **The code on Github has stopped updating in December 2017. The code of business version is continuously being updated. If you need license to obtain the latest code, please contact WeChat: 10890.**


## Terminology in VA ##
Terminology | Explanation
---- | ---
Host | The APP that integrates the VirtualAPP SDK is called  host.  
Host Plug-in | A host package is used to run another ABI on the same device. It also called plug-in package,extension package, host plug-in package, host extension package.
Virtual APP / VAPP | App installed in the VA space
External APP | App installed in the device
<br/>

## VA Technical architecture ##
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/va_architecture.jpg)  
VA technology involves the APP layer, Framework layer and Native layer of Android in total.
App must be installed on the system before it can run. The APP installed inside the VA  space is not actually installed into the system, so it cannot run. Then how to get it to run?
Answer: The only way to do this is to "cheat" the system into thinking it has been installed. This "cheat" process is the core work of the VA Framework, and is also the core technical principle of the VA.  

**Here is the description of what did each layer do:**

Layer | Main work
---- | ---
VA Space | An internal space is provided by the VA for the installation of the APP to be run inside it, and this space is system isolated.
VA Framework | This layer is mainly a proxy for Android Framework and VAPP, which is the core of VA. And VA provides a set of VA Framework of its own, which is between Android Framework and VA APP. </br>1. For VAPP, all the system services it accesses have been proxied by VA Framework, which will modify the request parameters of VAPP and send all the parameters related to VAPP installation information to Android Framework after changing them to the parameters of the host （Some of the requests will be sent to their own VA Server to be processed directly, and no longer send to the Android system）. This way Android Framework receives the VAPP request and checks the parameters, and it will think there is no problem.</br>2. When the Android system finishes processing the request and returns the result, the VA Framework will also intercept the return result and restore all the parameters that have been original modified to those that were sent during the VAPP request. This way the interaction between VAPP and Android system can work.
VA Native | The main purpose of this layer is to accomplish 2 tasks: IO redirection and the request modification for VA APP to interact with Android system. </br>1. IO redirection is some APPs may be accessed through the hard code absolute path. But if the APP is not installed to the system, this path does not exist. Through IO redirection, it will be redirected to the path to install inside VA.</br>2. In addition, there are some jni functions that cannot be hooked in VA Framework, so they need to be hooked in the native layer.
</br>

In summary:
As you can see from the above technical architecture, the internal VA APP actually runs on top of VA's own VA Framework. VA has intercepted all system requests from its internal APP, and through this technology it can also have full control over the APP, not just the multi space. And for the convenience of developers, VA also provides SDK and Hook SDK.


## VA Process architecture#
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/va_process.jpg)    
There are five types of processes in the VA’s runtime: CHILD process, VA Host Main process, VA Host Plugin process, VAPP Client process, and VAServer process. 
To support both 32-bit and 64-bit APPs, VA needs to install two packages: a master package and a plug-in package ( In this document, the main package is 32 bits and the plug-in package is 64 bit ).
Two packages are also necessary because a package can only run in one mode, either 32-bit or 64-bit. So for 32-bit APPs, VA uses the 32-bit main package to run, and for 64-bit APPs, VA uses the 64-bit plug-in package to run.
The main package contains all the code of VA, and the plug-in package contains only one piece of code that loads the main package code for execution, no other code. So plug-in package rarely needs to be updated, just the main package. 
In addition, whether the main package is chosen to use 32-bit or 64-bit can be modified in the configuration file ( For example, for users who want to access GooglePlay, it will be modified to 64-bit for the main package and 32-bit for the plug-in package ).

**The functions and explanations of the each type of process are as follows:**</br>

Process Type | Function
---- | ---
CHILD | Other processes integrated by VA Host, such as: keepalive process, push process, etc.
VA Host Main | The process where the UI main interface of the VA main package is located. The default main package is 32-bit and the plug-in package is 64-bit, which can be modified and switched in the configuration file
VA Host Plugin | The process that supports the plug-in package of 64-bit APP. The default main package is 32-bit and the plug-in package is 64-bit, which can be modified and switched in the configuration file.
VAPP Client | The process generated by the APP installed into VA after it starts, it will modify io.busniess.va:pxxx process name to the real process name of VAPP when it runs.
VAServer | The process where the VA Server is located, it is used to handle requests in VA that are not assigned to the system for processing, such as APP installation processing.
<br/>

## VA can satisfy almost all your needs ##
Through the above technical architecture, we can know that VA can fully control APP and provide Hook SDK, which can satisfy almost all your needs in various fields: 

1. Satisfy the need of **dual/multi space**   
VA allows you to install multiple WeChat/QQ/WhatsAPP/Facebook and other APPs on the same mobile phone, so you can have one phone with multiple accounts logged in at the same time.  

2. Satisfy the need of **mobile security**  
VA provides a set of internal and external isolation mechanisms, including but not limited to (file isolation / component isolation / process communication isolation). Simply speaking, VA internal is a "completely independent space". 
Through VA, work affairs and personal affairs can be safely separated without mutual interference. With a little customization, you can achieve mobile security-related needs such as application behavior audit, data encryption, data acquisition, data leakage prevention, anti-attack leaks and so on.    
    **2.1 Application behavior audit**  
The HOOK capability provided by VA can realize real-time monitoring of user usage behavior and upload violation information to the server. And it's easy to implement things like Time Fence ( whether a feature of the APP can be used in a certain time ), Geo Fence ( whether a feature of the APP can be used in a certain area ), sensitive keyword filtering interception and other functional requirements.    
    **2.2 Data encryption**    
The HOOK capability provided by VA can realize all data/file encryption of the application, ensuring data/file landing security.  
    **2.3 Data acquisition**           
The HOOK capability provided by VA can realize the demand for real-time silent upload of application data, such as chat records and transfer records, preventing them from being deleted afterwards without traceability.  
	**2.4 Data leakage prevention**  
The HOOK capability provided by VA can realize application anti-copy/paste, anti-screenshot/recording, anti-sharing/forwarding, watermark traceability and other requirements.   
	**2.5 Anti-attack leaks**  
With the application control capability provided by VA, privacy-related behaviors such as SMS/ address book/call log/ background recording/background photo/ browsing history and location information can be completely controlled in sandbox, prevent Trojan horses/malicious APPs from acquiring users' real private data, causing serious consequences such as leakage of secrets.
3. Satisfy the need of **ROOT without HOOK**  
VA provides Hook capability of Java and Native. With VA, you can easily achieve functions required by various scenarios, such as virtual positioning, changing device, APP monitoring and management, mobile security and so on.  

4. Satisfy the need of **silent installation**  
VA provides the ability to silently install, silently upgrade and silently uninstall APPs. For example, the application store or game center can be integrated with VA to avoid the need for users to manually click to confirm the installation operation, so that it can be installed into VA immediately after downloading, bringing users an experience like "small program" , completely avoiding the problem of applications not easily installed by users.  

5. Satisfy the need of **APP controlled**   
You can clearly grasp the system API, sensitive data, device information, etc. accessed by the APP through VA. For example, whether the APP accesses the contacts, photo albums, call log, whether it accesses the user's geographic location and other information.
Of course, you can also control or construct custom messages to these APPs via VA, and not only that, you can also get access to the APP's private data, such as chat database and so on. In a word, through the application control capability provided by VA, you can easily control all the behaviors of the APP, even modify the content of the APP and server interaction and so on .  </br>


6. Satisfy the need of **overseas markets**  
VA implements support for Google services to support overseas APPs running, such as Twitter, Messenger, WhatsAPP, Instagram, FaceBook, Youtube and so on.

7. Satisfy the need of **almost everything you can think of**  
VA has complete oversight and control over the internal APP, and can meet almost any of your needs！
8. VA is also the only commercially licensed product in this technology area   
**Hundreds of** licensed customers are currently paying to use the business version of VirtualAPP code, and the APP integrated with VirtualAPP code is launched more than 200 million times per day. Many Android engineers provide us with user feedback in different scenarios, and through our technical team's continuous optimization and iteration, we continue to improve product performance and compatibility.


VA Specialized capabilities
---

- Cloning ability<br/>
You can clone the APP already installed in the external system and run it internally without mutual interference. Typical application scenario is double space.

- Without installation ability<br/>
In addition to cloning already installed, VA can install (externally silent ) apk's directly internally and run them directly internally. Typical application scenarios are plug-in, standalone APP marketplace and so on.

- Double space ability<br/>
VA is not only "double space", but also has a unique multi-user mode that allows users to open the same APP internally for an unlimited number of times.

- Internal and external isolation ability<br/>
VA is a standard sandbox, or "virtual machine", that provides a set of internal and external isolation mechanisms, including but not limited to (file isolation/component isolation/process communication isolation). Simply put, the inside of a VA is a "completely separate space". Simply put, the inside of a VA is a "completely separate space". Based on it, you can realize a "virtual phone" on your cell phone with a little customization. Of course, you can also use your imagination to customize it for data encryption, data isolation, privacy protection, and enterprise management applications.

- Full control over internal APPs ability<br/>
VA has complete monitoring and control over the internal APP, which is absolutely impossible to achieve in an external environment without Root.

<details>
<summary>Details(Drop down to open)</summary>
  1. Service request control. First, VA directly provides some service request interception, you can easily customize these service requests when integrating VA, including but far from limited to (APP request to install apk / APP request to open certain files / APP request for location data / APP request for phone information, etc.)<br/><br/>
  2. System API control. VA virtualizes and implements the entire Android system framework, which is the principle that VA can run apk internally without installation. And you can through modify the virtual framework's implementation to dynamically monitor and analyze the behavior of the app, etc. In addition, you can also mock some system behavior to achieve some needs that are difficult to achieve externally (e.g. game controller).<br/><br/>
  3. Memory read and write. VA can read and write the memory of internal APP processes without Root.<br/><br/>
  4. Root without debugging. VA can debug (ptrace) internal APP processes without Root, based on which you can also achieve Root-free process injection.<br/><br/>
  5. Load arbitrary "plug-in" and "behaviors". The APP process inside VA is derived from the Client side code of the VA framework, so you can insert any "load" and "control" logic into the entry code of the process. These are very simple to implement.<br/><br/>
  6. Hook. VA has a set of built-in Xposed framework and native hook framework running on all versions of Android (until AndroidQ), based on it, you can easily Hook any Java/Native of any internal APP.<br/><br/>
  7. File control. VA built in a complete file redirection, which allows easy control of reading and writing of files from internal apps. Based on it, you can realize many functions such as protection and encryption of files can be achieved.<br/><br/>
  8. Note: The above control capabilities are implemented with code or examples for reference.
</details>


VA Other features
---

- High performance<br/>
Process-level "virtual machine", VA's unique implementation model makes its performance almost the same as that of the native APP, and does not need a long startup of ordinary virtual machines.

- Full version support<br/>
Support 5.0-13.0, 32-bit/64-bit APP, ARM and X86 processor. And support Android version in the future which will be updated.

- Easy Expansion and Integration<br/>
The integration of VA is similar to the normal Android library, even if your APP has been online, you can conveniently integrate VA and enjoy the capability brought by VA.

- Support Google services<br/>
Provide support for Google services in order to support overseas APPs.


## Comparison between VA and other technical solutions ##
When doing enterprise-level mobile security, it is often necessary to control the APP, and the following is a comparison of possible technical solutions listed： 

Technical solution | Principle introduction | Comment |  Running performance | Compatibility stability | Project maintenance cost
---- | --- | ---  | ---  | ---  | --- 
Repackage | Repackage the target APP by decompiling it and adding your own control code | 1. Nowadays, almost all APPs have hardened or tamper-proof protection, and repackaging is already a very difficult task</br> 2.The mobile phone system will also detect whether the APP is repackaged, if it is repackaged, it will directly prompt the user that there is a security risk, and even not allow the installation</br>3.For each APP, even each version to go deep to reverse analysis, time-consuming and difficult to maintain  | Excellent  | Poor  | High
Custom ROM | By customizing the system source code and compiling it to flash to the designated mobile phone | Only for specified internal mobile phones, too limited to be extended  | Excellent  | Excellent  | High
ROOT the mobile phone | By rooting the mobile phone，flashing a framework which is similar to Xposed | 1.Now, root the mobile phone is an unlikely thing</br> 2.In reality, it is difficult for users to root their own mobile phones  | Excellent  | Poor  | High
VA | Lightweight virtual machine with high speed and low device requirements | No risk point mentioned above  | Excellent  | Excellent. Hundreds of companies testing feedback at the same time  | Low. 
VA provides API and a professional technical team to ensure the stable operation of the project
<br/>
As you can see from the above comparison, VA is an excellent product and can reduce your development and maintenance costs.

## Integrating VA Steps ##
Step 1: Call the VA interface```VirtualCore.get().startup()```in your application to start the VA engine.  
Step 2: Call VA interface```VirtualCore.get().installPackageAsUser(userId, packageName)```to install the target APP into VA.
Step 3: Call VA interface```VActivityManager.get().launchApp(userId, packageName)```to start the APP.   
**With only the above 3 APIs to complete the basic use, VA has shielded the complex technical details and provided the interface API to make your development easy.**

## VA compatible stability ##
VA has been extensively tested by ** hundreds of **companies, including **high standards of testing and feedback of dozens of listed companies**, covering almost all types of equipment and scenarios at home and abroad, providing full protection for your stable operation!
 

Up to now, the supported system versions:

System version | Whether to support
---- | ---
5.0 | support
5.1 | support
6.0 | support
7.0 | support
8.0 | support
9.1 | support
10.0 | support
11.0 | support
12.0 | support
13.0 | support
<br/>


Supported App Types:

App Type | Whether to support
---- | ---
32-bit APP | support
64-bit APP | support
<br/>

Supported HOOK Types:

Hook Type | Whether to support
---- | ---
Java Hook | support
Native Hook | support

Supported CPU Types:

Hook Type | Whether to support
---- | ---
ARM 32 | support
ARM 64 | support
<br/>

## How to give feedback on problems encountered with integrated VA ? ##
After the purchase of the license we will establish a WeChat group, any problems can always feedback to us, and according to the priority in the first time to deal with.

## VA Development document ##
Please refer to the VA development documentation：[Development document](doc/VADev_eng.md)


License Instructions
------
VirtualApp virtual machine technology belongs to: Jining Luohe Network Technology Co., LTD. It applied for several VirtualApp intellectual property rights from 2015 to 2021 and` is protected by the Intellectual property Law of the People's Republic of China`.When you need to use the code on Github, **please purchase a business license**，and receive the full source code of the latest VirtualApp business version.Hundreds of licensed customers are paying to use the business version of VirtualApp code, and the app integrated with VirtualApp code is launched more than 200 million times a day. Many Android engineers provided us with user feedback in different scenarios, and through our technical team's continuous optimization and iteration, VirtualApp Business Edition code has better performance and higher compatibility. `The company of that year will become one of them after obtaining the license, and enjoy the technological achievements after the continuous iteration. And we can interact and collaborate with our licensed customers operationally, technically and commercially.`

<br/>
Person in charge: Mr. Zhang <br/>
WeChat：10890 <br/>
<br/>


Serious statement
------
If you use VirtualApp for **internal use, business profit or upload it to the application market**without licensing. We will take evidence and then report you to the police (for copyright infringement) or prosecute you. It will cause your company to undertake criminal liability and legal action, and affect your company's goodwill and investment.`Purchasing a business license can save you a lot of time developing, testing and refining compatibility, leaving you more time for innovation and profitability.`Luo He Technology has called to the police and sued a number of individuals and companies in 2020.<br/>

**In response to the national call for the protection of intellectual property rights! Anyone who reports that his or her company or other companies are using VirtualApp code to develop products without licensing will be given a cash reward upon verification. We will keep the identity of the whistleblower confidential! Reporting WeChat: 10890.**

  <br/>

Major updates of the business version
------

1. Support Android 13.0
2. Not easily misreported by anti-virus software
3. Framework optimization, performance greatly improved
4. Mobile system and APP compatibility greatly improved
5. Run Google services perfectly
6. Supports running pure 64-bit Apps
7. Built-in `XPosed Hook` framework
8. Add positioning mock code
9. Add code to change device 
10. Nearly 400 other fixes and improvements, please see the following table for detail

<br>

2017 - 2021 Business Edition Code Update Details
------
**July 27, 2022 to August 19, 2022 Business Edition Code Updatest**

413、Android 13.0 continues to adapt<br/>
412、Adjust the main version to 64bit<br/>
411、Fix the problem that the App cannot be opened on some Huawei mobile phones<br/>
410、Fix the problem that the application cannot be opened on OPPO 13.0<br/>
409、Fix the calling problem of Baidu Voice TTS<br/>
408、Fix the problem that the root directory of sdcard can still be accessed after data isolation<br/>
407、Repair the crash problem on Harmony mobile phone<br/>
406、Fix the problem of Hook failure in Debug mode<br/>
405、Add obfuscation of BinderInvocationStub to avoid the crash problem after obfuscation<br/>
404、Fix the problem that the native layer may crash when calling the free function<br/>
403、Fix the crash problem of WeChat due to the virtual file system<br/>


**July 9, 2022 to July 26, 2022 Business Edition Code Updatest**

402、Android 13.0 adaptation<br/>
401、Fix the problem of file path processing after virtual storage is enabled<br/>
400、Fix the problem that Notification does not prompt on 12.0<br/>

<details>
<summary>December 2017 to May 31, 2022 Business Edition code updates (Drop down to open)</summary>

**April 28, 2022 to May 31, 2022 Business Edition Code Updatest**

399、Fix the crash caused by onGetCallingUid hook<br/>
398、Repair the problem of WeChat 8.0.22 startup crash<br/>


**April 5, 2022 to April 27, 2022 Business Edition Code Updatest**

397、Remove some redundant hook codes in sandhook to prevent some apps from failing to start<br/>


**March 13, 2022 to April 5, 2022 Business Edition Code Updatest**

396、New function: Implement built-in media provider in VA to support media library isolation and other requirements<br/>
395、Fix WeChat/QQ crash when using voice<br/>
394、Bluetooth crash problem adaptation<br/>
393、Add some Log<br/>
392、Delete some useless codes<br/>


**January 22, 2022 to March 12, 2022 Business Edition Code Updatest**

391、Fix the problem that StorageManager is not hooked on Huawei mobile phones<br/>
390、Fix the problem that the latest version of WeChat cannot be started from the SD card<br/>
389、Add the processing of the requestedPermissionsFlags field in PackageInfo<br/>
388、Added VSettingsProvider to avoid exceptions caused by built-in applications without permission to operate settings provider<br/>
387、Fix WeChat and other APP startup black screen, ANR and other problems<br/>
386、Add hook to MediaProvider<br/>
385、Added the processing of plug-in shareUserId, so that the data of the plug-in can be configured to be placed in the main package<br/>
384、Added option to configure whether to disable Tinker<br/>
383、Fix Android 12 permission processing adaptation<br/>

**December 30, 2021 to January 21, 2022 Business Edition Code Updates**

382、Sandhook adapter 12.0<br/>
381、Fix the problem that Sandhook does not work on some 11.0<br/>
380、Added the compile option VA_FULL_EXT to control whether to compile the VA source code into the VA-EXT<br/>

**November 24, 2021 to December 29, 2021 Business Edition Code Updates**

379、Adapter Android 12.0<br/>
378、Add file permission check <br/>
377、Fixed the issue that static broadcast receivers could not receive broadcasts in independent processes<br/>
376、Fix the problem that WeChat may crash when logging in for the first time<br/>
375、Fix some App some apps could not display avatars<br/>
374、Fix VA crash on some oppo devices<br/>

**September 21, 2021 to November 23, 2021 Business Edition Code Updates**

373、Fix WhatsApp crash on some devices<br/>
372、Add broadcast test for VA<br/>
371、Fix the compatibility problem of Tik Tok Speed version<br/>
370、Fix readlinkat return value precision<br/>
369、Fix the problem of installing APP from outside, not referencing org.apache.http.legacy<br/>
368、Fix Huawei Nova 5i, 64-bit main package compatibility<br/>
367、Fix external storage redirection issue on 11.0<br/>
366、Fix the GMS login problem on 11.0<br/>
365、Fix 11.0 some APP read and write sdcard error problem<br/>
364、Fix the problem that APP may not open after the death of VA core process<br/>
363、Add the error log that can't start when no plug-in is installed<br/>


**August 22, 2021 to September 20, 2021 Business Edition Code Updates**

362、Horizontal screen re-adaptation<br/>
361、Fix the problem that some APPs cannot be opened after installation through file protocol<br/>
360、Fix the problem of Intent data loss in the Intent passed to JobIntentService<br/>
359、Fix the problem that the second call of JobIntentService does not work<br/>
358、Fix the problem of crashing some APPs on Huawei cell phones<br/>
357、Fix the game login problem on Xiaomi phone<br/>
356、Fix the problem that some applications cannot be opened after reinforcement<br/>
355、Add detection of associated start permission<br/>
354、targetSdk 30 adaptation<br/>
353、Fix the problem that some applications can't access the Internet when targetSdk is 30<br/>
352、Fix the problem that sdcard can't be accessed when targetSdk is 30<br/>
351、Use cmake to replace gradle task in compile script.<br/>
350、Remove obsolete documents<br/>

	
**August 7, 2021 to August 21, 2021 Business Edition code Updates**

349、Tweak and optimize gradle script<br/>
348、hidedenApiBypass support for Android R+<br/>
347、targetSdk 30 support<br/>
346、Fixthe bug that VIVO system service<br/>
345、Fix the bug that VIVO phone can't use camera<br/>
344、Fix dex loading abnormal state acquisition<br/>
343、Fix libart.so path problem on Android R<br/>
342、Fix the bug of Andoid Q+ delete notification<br/>
341、Fix the permission check of APN uri<br/>
340、Fix Android R suspend resume thread state<br/>
339、Fix some hook failure cases in debug mode<br/>
338、Fix some bugs of hook after R<br/>

**April 25, 2021 to August 6, 2021 Business Edition Code Updates**

337、Fix the problem that some phones cannot upload avatars in Tan Tan<br/>
336、Fix Android 10 Huawei device IO redirection problem<br/>
335、Adjust the horizontal and vertical screen logic, reduce the occurrence of abnormalities<br/>
334、Add the callback interface of Activity life cycle<br/>
333、Fix the broadcasting problem of Android 12<br/>
332、Fix the bug of abnormal status of some interfaces of WeChat<br/>
331、Fix the support of Outlook, One drive, Teams, Zoom and other overseas APPs.<br/>
330、Fix the bug Android 11 a permission request<br/>
329、Fix the problem that some cocos2d engines only display half screen<br/>
328、Fix the problem that WeChat can not send files under multi-user<br/>
327、split apk support<br/>
326、Android S support<br/>

**February 24, 2021 to April 24, 2021 Business Edition Code Updates**

325、Adapt to multi-user environment<br/>
324、Fix the compatibility problem of the new version of WeChat<br/>
323、Compatible with more enterprise level reinforcement<br/>
322、Support VAPP setting power source optimization<br/>
321、Fix missing permission statement<br/>
320、Fix the reference of android.test.base library on Android 11<br/>
319、Optimize ext plugin judgment<br/>
318、Optimize the selection of ABI during installation<br/>
317、Fix Google docs crash on Android 11<br/>

**October 15, 2020 to February 23, 2021 Business Edition Code Updates**

316、Solve the compatibility of the new version of Love Encryption, Bang Bang and other reinforcement<br/>
315、Fix the problem of WhatsApp not showing cold boot Splash<br/>
314、Optimize the recognition of system APP<br/>
313、Improve the support in multi-user environment<br/>
312、Solve the problem that ext plug-in is stuck in some cases<br/>
311、Support Google Play to download APP in VirtualAPP<br/>
310、Fix the problem that Android 11 QQ can not display pictures<br/>
309、Compatible with Android 11 running Google Service<br/>
308、Fix the problem that Android 11 can't run chromium<br/>
307、Support Hook @CriticalNative Method<br/>
306、Fix the problem that JDK 13 cannot be compiled and run<br/>
305、Fix the problem that Service may crash in some cases<br/>
304、Fix the problem that Android 11 cannot load private data of external storage<br/>
303、Fix the problem that low version APP cannot use org.apache.http.legacy<br/>
302、Fix the problem that the system task stack only shows the last one in some cases<br/>
301、Improve the build script for different platforms<br/>
300、Fix the problem that Android 11 cannot read obb<br/>
299、Fix the problem that the software is not backward compatible<br/>
298、Rebuild VAPP installation framework<br/>
297、Rebuild virtual file system<br/>
296、Fix the problem that WebView cannot be started under certain circumstances<br/>
295、Fix the bug of VAPP uninstall and reinstall<br/>
294、Fix the mobile game "LOL" login exception problem<br/>
293、Support the installation of Splits APK<br/>
292、Support dynamic configuration of the main package environment<br/>
291、Fix the problem of 32-bit QQ calling 64-bit WeChat delay<br/>
290、Fix the problem of Messenger calling Facebook crash<br/>
289、Optimize the support of Google service framework<br/>
288、Realize the new extension package synchronization mechanism<br/>
287、Fix the exception problem of Android 11 official version<br/>
286、Add system Package cache to optimize performance<br/>
285、Fix the bug that the disabled component can still be queried by PMS<br/>
284、Fix the problem of abnormal Launch behavior in some interfaces of WeChat<br/>
283、Fix the bug that ContentProvider.getCallingPackage returns Host package name<br/>
282、Fix the bug of uid virtualization and solve the problem that some app permission check fails<br/>
281、Rewrite the implementation of PendingIntent, IntentSender<br/>
280、Optimize process management, fix the long-standing probabilistic process deadlock problem<br/>
279、Rewrite Service implementation, Service life cycle more accurate, not easy to be killed<br/>


**September 13, 2020 to October 15, 2020 Business Edition Code Updates**

278、Fix the problem that 64-bit APP cannot call 32-bit APP<br/>
277、Fix the problem of loading HttpClient in Android R <br/>
276、Fix the problem of a crash in Android R debug mode<br/>

**August 23, 2020 to September 12, 2020 Business Edition Code Updates**

275、Add the missing service hook<br/>
274、Fix the problem that Baidu Translate cannot be started <br/>
273、Fix the problem that the split app downloaded by GP cannot be started<br/>

**July 10, 2020 to August 22, 2020 Business Edition Code Updates**

272、Fix Service creation<br/>
271、Add missing Hook for NotificationService<br/>
270、Fix Yotube crash<br/>

**May 19, 2020 to July 9, 2020 Business Edition Code Updates**

269、Preliminary adaptation of Android 11 beta1<br/>
268、Fix the problem of multi space flashback in RED<br/>
267、Fix the problem of "Application signature is tampered" reported by  multi space of some APPs<br/>

**April 24, 2020 to May 18, 2020 Business Edition Code Updates**

266、Fix sh call error<br/>
265、Fix the problem that Facebook cannot be logged in in the latest version of 9.0 or above<br/>
264、Help Enterprise WeChat to fix the problem that it can't take photos when starting virtual storage<br/>
263、Fix the problem that 64-bit APP can't open Activity in some cases<br/>

**March 24, 2020 to April 23, 2020 Business Edition Code Updates**

262、Fix the problem that Vivo device prompts to install game SDK<br/>
261、Fix the problem that Android Q cannot load some system so<br/>
260、Fix Huawei device microblog not responding<br/>
259、Ignore crashes caused by unnecessary permission checks<br/>
258、Fix the crash of WPS sharing files<br/>
257、Flashback issue in some 10.0 devices<br/>

**March 7, 2020 to March 23, 2020 Business Edition Code Updates**

256、Fix WeChat open two pages at the same time problem<br/>
255、Fix the problem that WeChat login successfully but return to the login page<br/>
254、Fix the problem that the latest version of QQ can not download attachments<br/>
253、Update SandHook version<br/>
252、Fix the problem of unsigned Apk installed above 9.0<br/>
251、Fix the positioning problem of 10.0<br/>

**January 16, 2020 to March 6, 2020 Business Edition Code Updates**

250、Tweak lib redirection logic<br/>
249、Fix crash issue on Samsung 10.0 systems<br/>
248、Fix hook exception in release build<br/>
247、Add SandHook proguard rules<br/>
246、Fix compatibility issue with VirtualApk in some APPs <br/>
245、Fixed VA internal request to install apk failed<br/>

**December 26, 2019 to January 15, 2020 Business Edition Code Updates**

244、Fix a missing hook in Android Q<br/>
243、Disable AutoFill in Emui10<br/>
242、Add new api to end all Activity<br/>

**December 15, 2019 to December 25, 2019 Business Edition Code Updates**

241、Fix the problem that enterprise WeChat and other apps cannot be launched on Emui10<br/>
240、Fix a possible crash in 4.x<br/>
239、Upgrade SandHook to fix Hook for Thread class<br/>
238、Fix the permission problem caused by some interfaces of Android Q<br/>

**November 20, 2019 to December 14, 2019 Business Edition Code Updates**

237、Fix crash caused by Notification cache<br/>
236、Fix classloader issue of high version Notification<br/>

**November 9, 2019 to November 19, 2019 Business Edition Code Updates**

235、Fix ART Hook for Android 5.x <br/>
234、Fix the deadlock problem caused by ART Hook <br/>

**November 2, 2019 to November 8, 2019 Business Edition Code Updates**

233、Fix WPS, NetEase Mail, etc. crashing on Q devices <br/>
232、Fix the problem that Tom Cat Run crashes on some Q devices <br/>
231、Fix the problem that QQ crashes on some Q devices <br/>

**October 25, 2019 to November 1, 2019 Business Edition Code Updates**

230、Fix cloning Google Play download of 64-bit APP<br/>
229、Fix Enterprise WeChat <br/>
228、Fix Telegram <br/>

**October 8, 2019 to October 24, 2019 Business Edition Code Updates**

227、Fix the exception of AppOspManager under Android P <br/>
226、Add the missing Hook of ActivityTaskManager under Android P <br/>
225、Fix the exception of Activity Top Resume under Android P <br/>
224、Support running in system multi-user mode <br/>

**October 8, 2019 Business Edition Code Updates**

223、Fix the issue of Android P or above internal app returning to desktop exception <br/>
222、64-bit branch support for Android Q <br/>

**September 20, 2019 to October 7, 2019 Business Edition Code Updates**

221、Fix the problem that the apk installed in the extension plugin cannot display the icon and name correctly <br/>
220、Fix the problem that twitter cannot be opened <br/>
219、Officially compatible with Android Q official version <br/>
218、Fix the problem that some Activity of Android Q cannot be opened again <br/>
217、Initially compatible with Android Q official version <br/>
216、Fix several bugs of 64-bit branch <br/>
215、Newly add 64-bit branch to support 32-bit plugins, the branch supports 32-bit old devices and 64-bit devices can support 32-bit old applications in the case of 32-bit plug-ins <br/>

**December 2017 to July 30, 2019 Business Edition Code Updates**

214、Improve APP layer prompt message<br/>
213、Improve some code<br/>
212、Fix the method of sending broadcast from host to plug-in <br/>
211、Compatible with the latest gradle plug-in <br/>
210、Add broadcast namespace to avoid multiple APPs that use VA technology to interfere with each other <br/>
209、Fix the problem that IMO can't be opened <br/>
208、Fix the problem that some ContentProvider cannot be found <br/>
207、Support pure 32-bit mode to be compatible with old devices <br/>
206、Preliminary support for pure 64-bit mode to cope with the change of Google Marketplace strategy in August <br/>
205、Adapt to Android Q beta4 <br/>
204、Fix the problem that Cargo LaLa can't be installed<br/>
203、Optimize the decision logic of 64-bit apk<br/>
202、Fix networking of App with network certificate configuration<br/>
201、Refactored component state management<br/>
200、Optimize MIUI/EMUI ContentProvider compatibility<br/>
199、Fix StorageStats Hook<br/>
198、Fix Kwai can't login<br/>
197、Fix YY can not start, better compatibility with plug-in framework<br/>
196、Fix Facebook login<br/>
195、Fix the problem of Google Play downloaded App can not find so (Royal War)<br/>
194、Fix split apk support<br/>
193、Fix Youtube can not start<br/>
192、Fix the problem that Youku can not start<br/>
191、Fix the bug that there may be broadcast namespace conflict between apps when multi space<br/>
190、Adopt a new strategy to bypass the Hidden Policy API after Android P<br/>
189、Adapted to Android Q(beta1)<br/>
188、Fix the problem that some APPs of Huawei devices cannot recognize the storage<br/>
187、Fix the problem that the startup process may fail dues to the APP cannot run<br/>
186、Fix the problem that some native symbols cannot be found in 4.4 devices<br/>
185、Fix the problem that some devices fail to get the package name of WebView<br/>
184、Fix the problem of Service detail processing<br/>
183、Optimize startup speed<br/>
182、Fix WebView loading failure in a few devices<br/>
181、Fix the problem of Lib decision<br/>
180、 Fix the problem that some Huawei models cannot read the sdcard<br/>
179、Fix the problem that Service may exist<br/>
178、Allow to determine whether Activity is started externally based on intent<br/>
177、Fix Gms and Google Play launching to incorrect environment on some models<br/>
176、Fix the compatibility problem caused by the newly implemented StaticBroadcast<br/>
175、Fix the problem that apache.http.legacy cannot be used on Android P<br/>
174、Implementation of Native trace<br/>
173、Optimize IO Redirect performance<br/>
172、Fix the problem that wechat can't connect to the network in some cases<br/>
171、Fix the small probability process attach of incorrect BUG<br/>
170、Start the next phase of ROADMAP<br/>
169、Fix the problem caused by the inability of Android P to register more than 1000 broadcasts<br/>
168、Fix the DeadLock that may cause ANR<br/>
167、Fix the problem that some apps fail to load so dynamically<br/>
166、Fix the problem that some models have black screen when opened for the first time under the without installation running environment<br/>
165、Compatible with many mainstream Android emulators<br/>
164、Optimize startup performance<br/>
163、Solve several memory leaks<br/>
162、Fix the problem of IO Redirect priority<br/>
161、Fix the problem of no network connection of Messenger for devices below 8.0<br/>
160、Fix the bug that the internal app is still retained when the external APP is uninstalled when double space<br/>
159、Fix the problem that some Tencent reinforcement cannot run<br/>
158、 Fix the bug that Instagram cannot login to Facebook<br/>
157、Fix the bug that the process may start repeatedly with small probability<br/>
156、Fix the bug that GET_PERMISSIONS does not get permission<br/>
155、Fix the bug that startActivityIntentSender<br/>
154、Fix the problem that some Activity cannot be started in Vivo devices<br/>
153、Fix the problem that app cannot call external APP to select files<br/>
152、Improve the compatibility of Android P<br/>
151、Compatible with Google service of Android P<br/>
150、Fix the problem that some functions of Messenger are abnormal<br/>
149、Improve IO Redirect<br/>
148、Adapt a lot of Gms, fix the problem that the process restarts infinitely during the operation of Gms<br/>
147、Realize the running mechanism of Service<br/>
146、Improve 64bit, provide some ROM configuration 64bit Engine permission API<br/>
145、Fix the Activity startup problem on 4.4 devices<br/>
144、Support excludeFromRecent property<br/>
143、Fix the problem that Instagram can't Facebook login<br/>
142、Fix the problem of Facebook first login flashback<br/>
141、Support to run Gms, Google play, Play game in 64-bit mode<br/>
140、Support downloading and installing APPs in Google play running without installation/in multi space<br/>
139、Fix the bug of DownloadManagerG<br/>
138、Fix the bug of restarting the interface when Google play returns to the upper level<br/>
137、Fix the problem of so decision in without installation mode<br/>
136、Optimize the build script to facilitate the introduction of the project<br/>
135、Fix the problem that mobile MM SDK can't start<br/>
134、Fix the bug of WeChat Shake<br/>
133、Fix the bug of ZTE device instability<br/>
132、Support IO Redirect under ARM64<br/>
131、Fix the bug that the internal app is not updated when the external app is updated in USE_OUTSIDE mode<br/>
130、Compatible with the latest Android 9.0 (code name: pie) and the four Preview versions released before the official version<br/>
129、Compatible with x86 devices with built-in houdini<br/>
128、WindowPreview technology, so that the app launch and the real app to achieve the same speed<br/>
127、New ActivityStack to improve the quality of APP running<br/>
126、Solve the problem of app running exception with adding Atlas Framework<br/>
125、Now you can define the specific behavior of the virtual app back to the desktop<br/>
124、APP in the double space dynamically updated with the system, no need to manually check<br/>
123、Support targetSdkVersion >= 26, but still can normally run the low version of the APP<br/>
122、Compatible with QDroid virtual engine of Tencent Game Manager (beta)<br/>
121、Extensive refactoring of the underlying code to greatly improve the running speed<br/>
120、Fix the problem that NetEase News cannot be cancelled after sharing to Weibo<br/>
119、Fix the problem that APP custom permission cannot be identified<br/>
118、Fix the problem that the MoJi weatherAPP can not start<br/>
117、Fix the problem that some government APPs cannot be started<br/>
116、See code for details of API changes<br/>
115、Fix the problem of Samsung series APPs calling each other<br/>
114、Fix the account problem of Xiaomi APP in non-Xiaomi system<br/>
113、Fix third-party calls such as share/send, the return page is not normal<br/>
112、Fix the problem of APP Store of QQ can not be installed<br/>
111、Call the third-party APP, encryption of uri<br/>
110、Adapt the front bangs<br/>
109、Adapt the hook of Xiaomi rom<br/>
108、Adapt Nubia recording problem<br/>
107、 Internal hover window permission control<br/>
106、Optimize the processing of custom notification bar<br/>
105、Fix the INCLUDE_CODE permission problem of Context<br/>
104、Adapt Huawei, oppo's corner mark<br/>
103、Fix the process restart problem of Baidu video<br/>
102、Fix some snapchat can not start problem<br/>
101、Adapt autofill service, such as piexl series<br/>
100、Improve the 64-bit io hook<br/>
99、Optimize the compatibility of hook library, add back dlopen<br/>
98、Move the 64-bit extension package so to the 32-bit main package. (After jni code change, please build once before Run)<br/>
97、Notification bar changes: adapt 8.1 notification channel; remove the application, remove all notifications of the application<br/>
96、Compatible with some APPs, need to set android:largeHeap=true<br/>
95、Fix the ffmpeg library video can not play the problem<br/>
94、Optimize horizontal and vertical screen switching<br/>
93、Lower the limitation of calling external Activity through Intent<br/>
92、Compatible with MG SDK<br/>
91、64-bit support is still in the development stage<br/>
90、Update obfuscation configuration APP/proguard-rules.pro, must add the rule -dontshrink<br/>
89、Optimize the mock device, for example: after mock, some APPs do not appear device verification<br/>
88、Improve dex2oat compatibility<br/>
87、Optimize mock positioning<br/>
86、Remove dlopen<br/>
85、targetVersion can be changed to 26: support targetVersion<23 app dynamic permission application, support targetVersion<24 file Uri<br/>
84、installPackage changed to default asynchronous form<br/>
83、In order to support 64-bit mode, change back to aidl<br/>
82、Remove SettingHandler can now dynamically set special rules, the rules will be stored, no need to repeat settings<br/>
81、Add 2 native_setup<br/>
80、Improve jobService compatibility<br/>
79、ShortcutService related: associated VASettings.ENABLE_INNER_SHORTCUT<br/>
78、For the sake of stability and running efficiency, remove the Bluetooth, wifi and undeclared permission adaptations of the previous version<br/>
77、Add APP starts abnormal broadcast Constants.ACTION_PROCESS_ERROR<br/>
76、Fix a few games horizontal screen judgment problem<br/>
75、Demo adds device mock<br/>
74、Adapt Vivo a custom permission (background pop-up), VA is to return a history acitivty to the foreground. Vivo needs this permission<br/>
73、If there is no Bluetooth permission, return to the default value (overseas use it)<br/>
72、Fix uid permission check problem<br/>
71、Security updates, file permission control for internal applications<br/>
70、Improve the compatibility of internal APP calls, third-party login, sharing<br/>
69、Automatic filtering of external ContentProvider without permissions<br/>
68、Add feature: internal APP permission check (closed by default)<br/>
67、Device mock: Build class and build.prop<br/>
66、Improve the compatibility of the APP progard<br/>
65、Adapt to Samsung wifimanager<br/>
64、Fix ipc framework a parameter passing problem (IPCMethod this class must be updated)<br/>
63、Fill the 7.0 notification bar hook<br/>
62、Fix 8.0 dynamic shortcut menu hook<br/>
61、SettingHandler adds a new adaptation interface, mainly adapted to a variety of games<br/>
60、Functional changes: google automatic installation to manual installation, to avoid the first start time too long<br/>
59、Prohibit access to an external ContentProvider<br/>
58、Adapt Huawei desktop icon number<br/>
57、Permission classification notes, mark the permissions that can be deleted<br/>
56、Add a switch for the APP in double space mode to follow external upgrade<br/>
55、Improve the APP jni compatibility<br/>
54、Improve compatibility with APP integration of other plug-in frameworks<br/>
53、Add setting interface to set according to package name<br/>
52、Increase the scope of Uri adaptation to support sharing and viewing files via Uri<br/>
51、Fix a problem in Samsung 8.0<br/>
50、Improve the compatibility of the app components that come with the system, better compatibility with chrome webview, google service.<br/>
49、Improve the stability of ART<br/>
48、Increase camera adaptation range<br/>
47、Support internal App shortcut management under 8.0<br/>
46、Fix exec exception<br/>
45、Improve stability (Fix WeChat login flashback)<br/>
44、Fix the WeChat database crash problem<br/>
43、Fix the crash problem of some 4.4 devices<br/>
42、Fix the background application easy to be killed, Tudou video black screen, Sina Weibo can not open, Youku twice return can not exit<br/>
41、Increase the application keepalive mechanism, APP double space is less likely to be killed<br/>
40、Optimize the performance of virtual engine startup<br/>
39、Compatible with most of the reinforcement, third-party APP compatibility compared to the previous version to improve 40% +<br/>
38、Fix the shortcut icon is incorrect under some roms<br/>
37、Compatible with previous component StubFileProvider<br/>
36、Adapt the virtual IMEI of some new roms<br/>
35、Improve process initialization code to increase stability<br/>
34、Add internal send Intent.ACTION_BOOT_COMPLETED broadcast, you can set the switch<br/>
33、Adapt the associated google play game, support the game using google login<br/>
32、Adapt the google service framework for android O<br/>
31、Adapt android O shortcut<br/>
30、Adapt to headset mode<br/>
29、Some roms on the size of the intent limit, demo add scaling shortcut icon code<br/>
28、Fix the bug in the case of multi space<br/>
27、Fix the bug of MediaController in some cases<br/>
26、Fix the error of StubFileProvider in 4.1.2<br/>
25、Share uri processing<br/>
24、Fix the callbacks of cross-app calls to Activity<br/>
23、Block switch of notification bar of foreground service<br/>
22、Companying doc<br/>
21、Improve the CHOOSE callback of intent inside VA<br/>
20、Android O notification bar adaptation 2<br/>
19、 ipc framework optimization, improve the accuracy of determining the survival of the binder<br/>
18、jni's log switch Android.mk:LOCAL_CFLAGS += -DLOG_ENABLE<br/>
17、Confusion configuration<br/>
16、Notification bar adaptation of Android O<br/>
15、Fix the problem of network lag in some APPs<br/>
14、Adaptation of android 8.0 dl_open (jni loading)<br/>
13、Fix the bug of Huawei emui8.0<br/>
12、Improve positioning<br/>
11、Set the phone information, imei disguise algorithm<br/>
10、Adapted to 8.0 a certain function (the main APP: whatsAPP)<br/>
9、Fix internal WeChat and other applications, can not update the picture, video<br/>
8、Demo add the installation of listening, automatically upgrade the clone mode applications<br/>
7、Adaptations of 7.0 file provider adaptations<br/>
6、Add positioning code<br/>
5、The code is optimized for the architecture<br/>
4、Different features from the open source version<br/>
3、Solve some problems of WeChat being blocked<br/>
2、Repaire some devices compatibility<br/>
1、Repaire 12 small bugs<br/>
</details>





