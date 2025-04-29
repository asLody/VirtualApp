
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
Support 5.0-16.0, 32-bit/64-bit APP, ARM and X86 processor. And support Android version in the future which will be updated.

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
14.0 | support
15.0 | support
16.0 | support
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
VirtualApp virtual machine technology belongs to: Jining Luohe Network Technology Co., LTD. It applied for several VirtualApp intellectual property rights from 2015 to 2025 and` is protected by the Intellectual property Law of the People's Republic of China`.When you need to use the code on Github, **please purchase a business license**，and receive the full source code of the latest VirtualApp business version.Hundreds of licensed customers are paying to use the business version of VirtualApp code, and the app integrated with VirtualApp code is launched more than 200 million times a day. Many Android engineers provided us with user feedback in different scenarios, and through our technical team's continuous optimization and iteration, VirtualApp Business Edition code has better performance and higher compatibility. `The company of that year will become one of them after obtaining the license, and enjoy the technological achievements after the continuous iteration. And we can interact and collaborate with our licensed customers operationally, technically and commercially.`

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

1. Support Android 16.0
2. Support Seccomp-Bpf.
3. Not easily misreported by anti-virus software
4. Framework optimization, performance greatly improved
5. Mobile system and APP compatibility greatly improved
6. Run Google services perfectly
7. Supports running pure 64-bit Apps
8. Built-in `XPosed Hook` framework
9. Add positioning mock code
10. Add code to change device 
11. Nearly 600 other fixes and improvements
<br>









