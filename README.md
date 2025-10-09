
[English Doc](README_eng.md "English")

<h1><p align="center">VA产品说明&开发指导</p></h1> 

## VA是什么? ##
VirtualApp(简称：VA)是一款运行于Android系统的沙盒产品，可以理解为轻量级的“Android虚拟机”。其产品形态为高可扩展，可定制的集成SDK，您可以基于VA或者使用VA定制开发各种看似不可能完成的项目。VA目前被广泛应用于APP多开、小游戏合集、手游加速器、手游租号、手游手柄免激活、VR程序移植、区块链、移动办公安全、军队政府数据隔离、手机模拟信息、脚本自动化、插件化开发、无感知热更新、云控等技术领域。<br> **Github上代码已在2017年12月份停止更新，商业版代码在持续更新中，如需授权获得最新代码，请联系微信：10890**


## VA中的术语 ##
术语 | 解释
---- | ---
宿主 | 集成VirtualApp类库（lib）的App叫做宿主  
宿主插件 | 用于在同一个手机,运行另一种ABI的宿主包,又称做插件包,扩展包,宿主插件包,宿主扩展包
虚拟App/VApp | VA的虚拟环境多开的app
外部App | 手机真实环境安装的app
<br/>

## VA技术架构 ##
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/va_architecture.jpg)  
VA技术一共涉及到了Android的APP层，Framework层以及Native层。  
一个APP想要在Android系统上运行，必须要安装后系统才会接纳。安装到VA内部的APP实际上并没有安装到系统中，所以正常情况下是无法运行的。那如何才能让它运行呢？  
答：那就只有“欺骗”系统，让系统认为已经安装。而这个“欺骗”过程就是VA Framework的核心工作内容，也是整个VA的核心技术原理。  

**下面介绍下在这3个层次分别做了什么事情：**

层次 | 主要工作
---- | ---
VA Space | 由VA提供了一个内部的空间，用于安装要在其内部运行的APP，这个空间是系统隔离的。
VA Framework | 这一层主要给Android Framework和VAPP做代理，这也是VA的核心。VA提供了一套自己的VA Framework，处于Android Framework与VA APP之间。</br>1. 对于VAPP，其访问的所有系统Service均已被 `VA Framework` 代理，它会修改VAPP的请求参数，将其中与VAPP安装信息相关的全部参数修改为宿主的参数之后发送给Android Framework（有部分请求会发送给自己的VA Server直接处理而不再发送给Android系统）。这样Android Framework收到VAPP请求后检查参数就会认为没有问题。</br>2. 待Android系统对该请求处理完成返回结果时，VA Framework同样也会拦截住该返回结果，此时再将原来修改过的参数全部还原为VAPP请求时发送的。</br>这样VAPP与Android系统的交互也就能跑通了。
VA Native | 在这一层主要为了完成2个工作，IO重定向和VA APP与Android系统交互的请求修改。</br>1. IO重定向是因为可能有部分APP会通过写死的绝对路径访问，但是如果APP没有安装到系统，这个路径是不存在的，通过IO重定向，则将其转向VA内部安装的路径。</br>2. 另外有部分jni函数在VA Framework中无法hook的，所以需要在native层来做hook。
</br>

总结：
通过上面技术架构可以看到，VA内部的APP实际是跑在VA自己的VA Framework之上。
VA已将其内部APP的全部系统请求进行拦截，通过这项技术也能对APP进行全面控制，而不仅仅只是多开。并且为了方便开发者，VA还提供了SDK以及Hook SDK。  


## VA进程架构 #
![](https://cdn.jsdelivr.net/gh/xxxyanchenxxx/temp@1.0/doc/va_process.jpg)    
VA运行时有5类进程：CHILD进程，VA Host Main进程，VA Host Plugin进程，VAPP Client进程，VAServer进程。
VA为了同时支持32位APP与64位APP，需要安装2个包：一个主包，一个插件包(在本文档中主包是32位，插件包是64位)。
2个包也是必须的，因为一个包只能运行在一种模式下，要么32位，要么64位。所以对于32位的APP，VA使用32位的主包去运行，对于64位的APP，VA则使用64位的插件包去运行。
在主包中含了VA的所有代码，插件包中只有一段加载主包代码执行的代码，无其他代码。所以插件包几乎不用更新，只需要更新主包即可。
另外主包是选择用32位还是64位，可以在配置文件中修改(比如对于要上GooglePlay的用户，会修改为主包64位，插件包32位)。

**各类进程的作用与解释如下：**</br>

进程类型 | 作用
---- | ---
CHILD | 由VA Host集成的其他进程，比如：保活进程，推送进程等。
VA Host Main | VA主包的UI主界面所在的进程。默认主包是32位，插件包是64位，可在配置文件中修改切换。
VA Host Plugin | 支持64位APP的插件包所在进程。默认主包是32位，插件包是64位，可在配置文件中修改切换。
VAPP Client | 安装到VA中的APP启动后产生的进程，在运行时会将io.busniess.va:pxxx进程名修改VAPP的真实进程名。
VAServer | VA Server的所在的进程，用于处理VA中不交予系统处理的请求。比如APP的安装处理。
<br/>

## VA几乎能满足您的一切需求 ##
通过上面的技术架构，我们可以了解到VA可以对APP进行全面的控制，并且提供了Hook SDK，几乎能满足您在各个领域的一切需求：  
1. 可以满足您的**双开/多开**需求    
VA可以让您在同一部手机上安装多个微信/QQ/WhatsApp/Facebook等APP，实现一部手机，多个账号同时登录。  

2. 可以满足您的**移动安全**需求  
VA提供了一整套内部与外部的隔离机制，包括但不限于(文件隔离/组件隔离/进程通讯隔离)，简单的说VA内部就是一个“完全独立的空间”。
通过VA可将工作事务与个人事务安全的隔离，互不干扰。稍作定制即可实现应用行为审计、数据加密、数据采集、数据防泄漏、防攻击泄密等移动安全相关的需求。    
    **2.1 应用行为审计**  
通过VA提供的HOOK能力可以实现实时监测用户使用行为，将违规信息上传到服务器；并能轻易实现诸如时间围栏(在某个时间段内能否使用应用的某个功能)、地理围栏(在某个区域内能否使用应用的某个功能)、敏感关键字过滤拦截等功能需求。  
	**2.2 数据加密**  
通过VA提供的HOOK能力可以实现对应用的全部数据/文件加密，保证数据/文件落地安全。   
	**2.3 数据采集**  
通过VA提供的HOOK能力可以实现应用数据的实时无感上传需求，如聊天记录、转账记录等，防止事后删除无法追溯。  
	**2.4 数据防泄漏**  
通过VA提供的HOOK能力可以实现应用防复制/粘贴、防截屏/录屏、防分享/转发、水印溯源等需求。   
	**2.5 防攻击泄密**  
通过VA提供的应用管控能力可以将APP获取短信/通讯录/通话记录/后台录音/后台拍照/浏览历史/位置信息等隐私相关的行为完全控制在沙盒中，防止木马/恶意APP获取到用户真实的隐私数据，造成泄密等严重后果。  

3. 可以满足您的**免ROOT HOOK**需求  
VA提供了Java与Native的Hook能力，通过VA，您可以轻易实现诸如虚拟定位、改机、APP监控管理、移动安全等各种场景需要的功能。  

4. 可以满足您的**APP静默安装**需求  
VA提供了APP静默安装，静默升级，静默卸载的能力。如应用商店或游戏中心在集成VA后可以避免需要用户手动点击确认安装的操作，做到下载后立即安装到VA内，给用户带来“小程序”搬的体验，彻底避免了应用不易被用户安装上的问题。  

5. 可以满足您的**APP管控**需求  
您可以通过VA清楚的掌握APP访问了哪些系统API，哪些敏感数据，哪些设备信息等。比如APP是否访问了联系人，相册，通话记录，是否访问了用户的地理位置等信息。
当然，您还可以通过VA控制或者构造自定义的信息给这些APP。不仅于此，您还可以获取到APP的私有数据，比如聊天数据库等。总之通过VA提供的应用管控能力，您可以轻易控制APP的一切行为，甚至修改APP与服务器交互内容等。  </br>


6. 可以满足您的**海外市场**需求  
VA实现了对Google服务的支持，以支持海外的App运行，比如Twitter、Messenger、WhatsApp、Instagram、FaceBook、Youtube等。  


7. 可以满足您的**VR程序移植**需求  
可以通过VA的Hook能力拦截VR设备的API，让您无需改动代码即可将VR程序移植到新的设备。  

8. 可以满足您**几乎一切能想到**的需求  
VA对于内部的App具有完全的监管和控制能力，几乎能满足您的一切需求！

9. 同时VA也是该技术领域**唯一一款**对外商业授权的产品    
截止目前已有**上百家**授权客户在付费使用VirtualApp商业版代码，集成VirtualApp代码的APP**日启动**次数**超过2亿次**，众多安卓工程师向我们提供不同场景下的用户反馈，通过我们技术团队不断优化迭代，不断提升产品性能与兼容性！


VA的特有能力
---

- 克隆能力<br/>
可以克隆外部系统中已经安装的App，并在内部运行，互不干扰。典型应用场景为App双开。

- 免安装能力<br/>
除了克隆已安装之外，VA可以直接在内部安装(外部无感知)apk，并在内部直接运行。典型应用场景为插件化，独立应用市场等。

- 多开能力<br/>
VA不仅可以“双开”，独特的多用户模式支持用户在内部无限多开同一个App。

- 内外隔离能力<br/>
VA是一个标准的沙盒，或者说“虚拟机”，提供了一整套内部与外部的隔离机制，包括但不限于(文件隔离/组件隔离/进程通讯隔离)，简单的说VA内部就是一个“完全独立的空间”。在此基础之上，稍作定制即可实现一部手机上的“虚拟手机”。当然您也可以发挥想象，定制成应用于数据加密，数据隔离，隐私保护，企业管理的应用系统。

- 对于内部App的完全控制能力<br/>
VA对于内部的App具有完全的监控和控制能力，这点在未Root的外部环境中是绝对无法实现的。
<details>
<summary>详细(下拉打开)</summary>
  1. 服务请求控制，首先VA直接提供了一些服务请求的拦截，您可以在集成VA的时候方便的定制这些服务请求，包括但远不限于(App请求安装apk/App请求打开某些文件/App请求定位数据/App请求手机信息等等)<br/><br/>
  2. 系统API控制，VA虚拟并实现了整个安卓系统框架，这也是VA可以免安装在内部运行apk的原理，您可以对虚拟框架的实现进行修改以动态监测分析App的行为等；除此之外，您还可模拟一些系统行为以实现一些在外部难以实现的需求(例如游戏手柄)。<br/><br/>
  3. 内存读写，VA可以无需Root读写内部App进程的内存。<br/><br/>
  4. 免Root调试，VA可以免Root调试(ptrace)内部的App进程，基于此您还可以实现免Root的进程注入。<br/><br/>
  5. 加载任意“插件”和“行为”，VA内部的App进程由VA的框架Client端代码派生而来，所以您可以在进程的入口代码插入任何“加载”以及“控制”逻辑。这些实现都非常简单。<br/><br/>
  6. 方法Hook，VA内置了一套运行于Android各个版本(直到AndroidQ)的Xposed框架以及native hook框架，基于此您可以方便的Hook任意内部App的任意Java/Native方法。可以轻松实现一个免Root的Xposed环境(有实例代码)。<br/><br/>
  7. 文件控制，VA内置了完整的文件重定向，可以方便的控制内部App的文件的读写，基于此可以实现对文件的保护加密等功能。<br/><br/>
  8. 注：以上控制能力均有实现代码或者实例以作参考。
</details>


VA的其他特性
---

- 高性能<br/>
进程级“虚拟机”，VA独特的实现方式让其性能几乎于原生App一致，更不需要普通虚拟机漫长的启动。

- 全版本支持<br/>
支持5.0-16.0，支持32位/64位app，支持ARM以及X86处理器。并且支持未来将更新的Android版本。

- 易扩展与集成<br/>
VA 的集成方式与普通Android库类似，即使您的App已经完成上线，您也方便的可以集成VA，享受VA带来的能力。

- 支持Google服务<br/>
提供Google服务的支持，以支持海外的App


## VA与其他技术方案对比 ##
在做企业级移动安全时，往往需要对APP进行管控，以下是列出的可能技术方案对比： 

技术方案 | 原理简介 | 点评 |  运行性能 | 兼容稳定性 | 项目维护成本
---- | --- | ---  | ---  | ---  | --- 
二次打包 | 通过反编译目标APP，加入自己的控制代码，重新打包 | 1.现在的APP几乎都有加固或防篡改保护，重打包已是一件非常困难的事</br> 2.手机系统也会检测APP是否被重打包，如果重打包，会直接提示用户存在安全风险，甚至不让安装</br>3.针对每一个APP，甚至每一个版本都要深入去逆向分析，耗时耗力，难于维护  | 优秀  | 差  | 高
定制ROM | 通过定制系统源码，编译刷到指定手机 | 只能针对指定的内部手机，局限性太大，无法扩展  | 优秀  | 优秀  | 高
ROOT手机 | 通过ROOT手机，刷入xposed等类似框架 | 1.ROOT手机现在本身已是一件不太可能的事</br> 2.现实中也很难让用户能去ROOT自己的手机  | 优秀  | 差  | 高
VA | 轻量级虚拟机，速度快，对设备要求低 | 无上述风险点  | 优秀  | 优秀，有上百家企业在同时测试反馈  | 低，VA提供了API并有专业的技术团队保障项目稳定运行
<br/>
通过以上对比可以看出，VA是一款优秀的产品，并且能降低您的开发维护成本！

## 集成VA步骤 ##
第1步：在您的Application中调用VA接口```VirtualCore.get().startup()```来启动VA引擎  
第2步:调用VA接口```VirtualCore.get().installPackageAsUser(userId, packageName)```将目标APP安装到VA中  
第3步:调用VA接口```VActivityManager.get().launchApp(userId, packageName)```启动APP    
**仅通过以上3个API就完成了基础使用，VA已屏蔽了复杂的技术细节，并提供了接口API，让您的开发变得很简单！**

## VA的兼容稳定性 ##
VA已被**上百家**企业进行了广泛测试，包含**数十家上市公司高标准**的测试及反馈，几乎涵盖了海内外的各种机型设备和场景！
为您的稳定运行提供了充分的保障！  

截止目前，支持的系统版本:

系统版本 | 是否支持
---- | ---
5.0 | 支持
5.1 | 支持
6.0 | 支持
7.0 | 支持
8.0 | 支持
9.1 | 支持
10.0 | 支持
11.0 | 支持
12.0 | 支持
13.0 | 支持
14.0 | 支持
15.0 | 支持
16.0 | 支持
<br/>


支持的APP类型:

APP类型 | 是否支持
---- | ---
32位APP | 支持
64位APP | 支持
<br/>

支持的HOOK类型:

Hook类型 | 是否支持
---- | ---
Java Hook | 支持
Native Hook | 支持

支持的CPU类型:

Hook类型 | 是否支持
---- | ---
ARM 32 | 支持
ARM 64 | 支持
<br/>

## 集成VA遇到问题如何反馈？ ##
购买授权后我们会建立微信群，有任何问题可以随时反馈给我们，并根据优先级在第一时间处理！

## VA开发文档 ##
VA开发文档请参考：[开发文档](doc/VADev.md)



授权说明
------
VirtualApp虚拟机技术归属于：山东盒一网络科技有限公司（原：济宁市罗盒网络科技有限公司），于2015年至2025年申请多项VirtualApp知识产权，`受中华人民共和国知识产权法保护`。当您需要使用Github上的代码时，**请购买商业授权**，获取商业授权后将可以收到最新VirtualApp商业版全部源代码。上百家授权客户在付费使用VirtualApp商业版代码，集成VirtualApp代码的APP日启动次数超过2亿次，众多安卓工程师向我们提供不同场景下的用户反馈，通过我们技术团队不断优化迭代，VirtualApp商业版代码性能更好、兼容性更高。`当您的公司获取授权后，将成为其中一员，享受这些不断迭代完善后的技术成果。并可以和我们的授权客户进行运营、技术及商业上的互动合作。`

<br/>
负责人：张总 <br/>
微信：10890 <br/>
<br/>


严重声明
------
您如果未经授权将VirtualApp用于**内部使用、商业牟利或上传应用市场**，我们将取证后报警（侵犯著作权罪）或起诉，这将对您所属公司造成刑事责任及法律诉讼，影响到您公司的商誉和投资。`购买商业授权为您节省大量开发、测试和完善兼容性的时间，让您更多时间用于创新及盈利。`罗盒科技已在2020年报警和起诉了一些个人及公司。<br/>

**为响应国家对于知识产权的保护号召！凡举报自己所在公司或其他公司未经授权，违法使用VirtualApp代码开发产品的，一经核实给予现金奖励。我们会对举报人身份保密！举报联系微信：10890**

  <br/>

商业版主要更新
------

1. 兼容最新Android 16.0
2. 支持Binder拦截，不再使用动态代理
3. 支持Seccomp-Bpf拦截
4. 不易被杀毒软件误报
5. 框架优化，性能大幅提升
6. 手机系统及APP兼容性大幅提升
7. 完美运行Google服务
8. 支持运行纯64位App
9. 内置`XPosed Hook`框架
10. 增加定位模拟代码
11. 增加改机代码
12. 其他600+项问题的修复和改进，详情请见下表

<br>

2017年-2025年商业版代码更新详细
------

**2025年9月18号 至 2025年 10月10号 商业版代码更新内容**

633、处理onNewIntent()中的activity Referrer<br/>
632、适配NotificationProviderPublic<br/>
631、修复15.0+上isDirectlyHandlingTransaction()的多线程处理<br/>
630、隐藏Libcore.os的反射获取<br/>
629、binderproxy模式支持IBatteryStats<br/>
628、适配某些加固APP<br/>


**2025年9月2号 至 2025年 9月17号 商业版代码更新内容**

627、Android 16kb page size 适配<br/>
626、fix蓝牙几个代理类的代码错误<br/>
625、处理某些APP在Application->attach中获取到宿主Application的场景<br/>
624、处理Activity referrer相关的<br/>
623、处理processOutsideIntent时intent包含vapp class的情况<br/>
622、针对某些oppo 13.0机型适配<br/>
621、处理调用ArrayUtils.indexOf时，有的地方按0开始起步有的地方按1开始起步导致异常，都统一为1<br/>
620、适配LockSettings/WifiScanner/NetworkScoreManager/WifiManager/SensitiveContentProtectionManager等几个manager<br/>
619、新功能模式下BinderProxyInjectManager.addInjector增加判断，避免有些因为class不存在导致crash<br/>
618、新功能模式下支持Instrumentation注入<br/>
617、增加对native层获取宿主信息的处理<br/>

<details>
<summary>2017年 12月 至 2025年 9 月 1 日 商业版代码更新内容(下拉打开)</summary>  <br/>


**2025年8月9号 至 2025年 9月1号 商业版代码更新内容**

616、增加对setxattr/lsetxattr/bind/connect/syscall等几个libc api处理<br/>
615、增加对execve启动的进程实现inline hook<br/>
614、增加可对application中的全部路径模拟为系统安装路径<br/>
613、增加对logcat等命令的过滤处理<br/>
612、修复app 崩溃/anr等系列问题<br/>
611、修复ParceledListSlice.getList返回Null的问题<br/>

**2025年7月22号 至 2025年 8月8号 商业版代码更新内容**

610、增加对INetworkScoreService的处理<br/>
609、修复新功能在release下的一些错误<br/>
608、修复某些手机上新功能无法打开APP	<br/>
607、修复Tethering相关的crash


**2025年7月3号 至 2025年 7月21 商业版代码更新内容**

606、与新功能相关的路径处理<br/>
605、一些bug处理<br/>

**2025年5月1号 至 2025年 7月2号 商业版代码更新内容**

604、新功能开发:基于拦截Binder的方式来实现对系统AIDL调用的拦截。通过这种方式可不再使用动态代理，对于稳定性将会有比较大提升<br/>
603、16.0最新版本继续适配<br/>


**2025年4月16号 至 2025年 4月30号 商业版代码更新内容**

602、对Android 16.0 beta 4 适配<br/>
601、完善seccomp-bpf重定向相关的功能<br/>
600、增加配置可以让APP只使用seccomp-bpf,不使用inline hook<br/>
599、将target sdk升级到34<br/>
598、修复va core进程由于client为空导致的crash<br/>
597、启动进程的时候增加重试，避免在某些机型上由于进程死亡太频繁导致无法启动进程<br/>
596、对于某类型加固后是否需要安装provider的部分改为动态判断<br/>
595、修复demo在某些设备上由于title为null导致的crash<br/>


**2025年3月27号 至 2025年 4月15号 商业版代码更新内容**

594、修复GMS无法调起登录的问题<br/>
593、增加对IInputMethodManagerGlobalInvoker的hook<br/>
592、修复工作空间中无法打开VAPP的问题<br/>
591、适配微信8.0.57<br/>

**2025年3月20号 至 2025年 3月26号 商业版代码更新内容**

590、增加对AppSearchManager的适配<br/>
589、增加对DomainVerificationManager的适配<br/>
588、增加对SystemUpdateManager的适配<br/>
587、修复多个进程同时启动同一个进程时的crash问题<br/>


**2025年2月28号 至 2025年 3月19号 商业版代码更新内容**

586、seccomp 相关的调整<br/>
585、修复微信在14.0+上开启seccomp-bpf无法打开的问题<br/>
584、新增对StorageStatsManager的适配<br/>
583、UsageStatsManager相关API适配<br/>

**2025年2月11号 至 2025年 2月27号 商业版代码更新内容**

582、适配最新版微信<br/>


**2025年1月24号 至 2025年 2月10号 商业版代码更新内容**

581、对IO进行inline hook时暂停所有Java线程,避免冷启动时因多线程导致的低概率crash<br/>


**2025年1月8号 至 2025年 1月23号 商业版代码更新内容**

580、installer静默安装部分适配<br/>
579、修复静态广播收不到消息<br/>
578、修复pending intent数据丢失问题<br/>
577、input manager 14.0+上的适配<br/>
576、蓝牙适配<br/>
575、queryStatsForPackage适配<br/>
574、修复有些手机上显示不出应用列表<br/>
573、其他一些小问题适配<br/>


**2024年12月21号 至 2025年 1月7号 商业版代码更新内容**

572、Seccomp-bpf支持32位<br/>
571、修复某些情况下路径多次重定向的问题<br/>
570、修复抖音人脸识别时可能白屏的问题<br/>
569、去掉几年前为抖音打不开做的一些修改<br/>


**2024年12月7号 至 2024年 12月20号 商业版代码更新内容**

568、修复startIntentSenderFoeResult无法工作的问题<br/>
567、修复GMS由于StatsManager无法hook导致crash的问题<br/>


**2024年11月27号 至 2024年 12月6号 商业版代码更新内容**

566、修复release打包后IJobService中的onNetworkChanged等几个函数被混淆导致找不到crash的问题<br/>
565、修复由于BluetoothAdapter.sService为null导致IBluetooth hook失败的问题<br/>
564、修复packagesettings被覆盖的问题<br/>
563、删除getCallingUid()中的缓存代码<br/>
562、Seccomp条件判断时处理Application.name为Null的情况<br/>
561、AGP升级到8.2.0<br/>

**2024年11月12号 至 2024年 11月26号 商业版代码更新内容**

560、重定向路径调整，区分/data/data/com.xxx以及/data/user/0/com.xxx，使其更符合APP使用实际使用的路径<br/>
559、适配fixupAppDir<br/>
558、reverseRedirectedPath增加检查，防止路径多次重复转换<br/>
557、修复splitNames信息缺失导致部分应用无法正常使用<br/>
556、补充一批normal权限,解决部分APP因为权限丢失无法正常使用的问题<br/>


**2024年10月31号 至 2024年 11月11号 商业版代码更新内容**

555、新功能：增加Seccomp-Bpf支持，实现更底层的拦截<br/>
554、增加对部分加固APP的支持<br/>
553、系统OTA升级时对Split Apks重新安装<br/>


**2024年10月14号 至 2024年 10月29号 商业版代码更新内容**

552、所有手机package.ini版本升级到7，避免某些情况下出现应用丢失<br/>
551、修改Demo包名<br/>


**2024年9月15号 至 2024年 10月13号 商业版代码更新内容**

550、移除对Sandhook的依赖<br/>
549、移除几处hook，避免部分机型crash<br/>
548、移除未使用到的goAsync，避免ANR<br/>
547、移除部分对1.X的升级处理废弃代码<br/>
546、修复手机系统升级到13.0+上后，应用列表消失的问题<br/>


**2024年8月23号 至 2024年 9月14号 商业版代码更新内容**

545、修复IO重定向中一系列函数未对dfd正确处理<br/>


**2024年8月9号 至 2024年 8月22号 商业版代码更新内容**

544、修复Native的一个空指针问题<br/>
543、修复Native的某个函数由于栈上随机数导致判断出错<br/>
542、修复unity中的检测问题<br/>
541、修复publishService crash问题<br/>
540、修复getPid死循环崩溃问题<br/>

**2024年8月3号 至 2024年 8月8号 商业版代码更新内容**

539、修复微信等APP因为webview导致的crash问题<br/>

**2024年7月19号 至 2024年 8月2号 商业版代码更新内容**

538、修复sandhook崩溃问题<br/>
537、修复sandhook hook不起效问题<br/>
536、替换sandhook中inline hook部分<br/>
535、修复android.permission.DETECT_SCREEN_CAPTURE权限导致的crash问题<br/>
534、修复静态广播导致的crash问题<br/>
533、修复百度搜索crash的问题<br/>
532、修复ResolveActivity跳转到外部应用时没有过滤export为false的场景导致crash<br/>
531、修复在某些华为设备上微信白屏的问题<br/>
530、修复微信流量异常的问题<br/>
529、class_linker适配android 15<br/>
528、修复 readlinkat参数问题导致的crash<br/>
527、解决某些unity路径检测的问题<br/>


**2024年7月3号 至 2024年 7月18号 商业版代码更新内容**

526、适配了几十个API,很大程度提升了稳定性<br/>
525、调整stopService不再走initProcess流程，解决了某些情况下的死锁问题<br/>
524、修复 startprocess启动App后再次进入VActivityManagerService导致死锁的问题<br/>
523、修复锁屏/亮屏广播引起的crash问题<br/>


**2024年6月17号 至 2024年 7月2号 商业版代码更新内容**

522、AttributionSoure中的uid调整 <br/>
521、修复微信注册，找回账号等几个界面白屏的问题<br/>


**2024年6月4号 至 2024年 6月16号 商业版代码更新内容**

520、将内置的Java hook框架SandHook调整为可选配置 <br/>
519、修复VA_ENABLE_EXT_FULL_SOURCE_CODE功能选项开启时，加载so错误的问题<br/>


**2024年5月8号 至 2024年 6月3号 商业版代码更新内容**

518、修复微信在鸿蒙4.0+上无法使用的问题 <br/>
517、调整VA demo package name<br/>



**2024年4月20号 至 2024年 5月7号 商业版代码更新内容**

516、适配华为账户登录和授权登录等功能 <br/>
515、适配荣耀账户登录和授权登录等功能 <br/>
514、修复Service中getApplicationContext返回Null的问题<br/>


**2024年4月4号 至 2024年 4月19号 商业版代码更新内容**

513、修复link&unlink参数没有处理重定向的问题 <br/>
512、修复AutoFillManagerStub未生效问题 <br/>
511、适配高版本ShadowJobService <br/>

	
**2024年3月7号 至 2024年 4月2号 商业版代码更新内容**

510、修复数款游因为戏二次注册provider导致无法打开 <br/>



**2024年2月19号 至 2024年 3月6号 商业版代码更新内容**

509、修通知跳转Crash <br/>
508、AMS API适配<br/>
507、DevicePolicyManager API适配<br/>
506、BlueTooth API适配<br/>
505、修复抖音crash问题<br/>


**2024年1月25号 至 2024年 2月18号 商业版代码更新内容**

504、修复抖音在部分手机无法打开的问题<br/>
503、修复抖音在部分手机运行一小段时间后崩溃的问题<br/>
502、修复抖音在部分手机crash后一直打不开的问题<br/>
501、修复抖音极速版在部分手机无法打开的问题<br/>
500、修复抖音极速版在部分手机运行一小段时间后崩溃的问题<br/>
499、修复抖音极速版在部分手机crash后一直打不开的问题<br/>
498、UserManager相关API适配<br/>
497、PackageManager相关API适配<br/>
496、Notification相关API适配<br/>
495、FingerprintManager相关API适配<br/>


**2024年1月5号 至 2024年 1月24号 商业版代码更新内容**

494、Activity Token获取适配<br/>
493、适配最新版微信<br/>

**2023年12月21号 至 2024年 1月4号 商业版代码更新内容**

492、适配libc可能没有R权限的情况<br/>


**2023年12月5号 至 2023年 12月20号 商业版代码更新内容**

491、修复储存空间异常的问题<br/>


**2023年10月24号 至 2023年 12月4号 商业版代码更新内容**

490、取消对Xposed依赖<br/>
489、适配最新版微信<br/>
488、适配setCompatibilityVersion<br/>
487、取消hookGetCallingUid对xposed的依赖<br/>
486、蓝牙适配<br/>
485、AddToDisplayAsUser处理<br/>
478、PendingIntent适配<br/>
484、MediaRecorder适配<br/>
483、处理dispatchVolumeKeyEvent API<br/>
482、修复AttributionSource cast crash<br/>
481、增加配置：是否优先使用外部app<br/>
480、修复启动前台service crash<br/>
479、修复14.0上renameat太短导致hook后覆盖其他函数的问题<br/>


**2023年10月8号 至 2023年 10月23号 商业版代码更新内容**

478、修复Annotation依赖包为空的问题<br/>
477、修复抖音等APP由于动态框架导致无法打开Activity的问题<br/>
476、修复纯Java APP在64位下以32位模式安装的问题<br/>
475、修复了13.0+上的class linker偏移检测问题<br/>
474、调整默认使用isUseRealDataDir模拟真实路径<br/>
473、JobServiceStub适配<br/>
472、IO重定向增加对renameat2的hook<br/>
471、修复APK安装模式下某些APP拍照黑屏<br/>
470、修复APK安装模式下微信小程序无法使用的问题<br/>

	
**2023年9月16号 至 2023年 10月7号 商业版代码更新内容**

469、移除已经废弃的虚拟定位代码<br/>
468、修复WhatsApp 来电通知bug<br/>
467、修复GMS相关问题<br/>
466、修复WhatsApp无法跳过短息验证界面问题<br/>
465、修复WhatsApp等部分APP启动后界面白屏问题<br/>
464、适配Alarms 相关API


**2023年9月6号 至 2023年 9月15号 商业版代码更新内容**

463、14.0上JobScheduler API适配<br/>
462、修复从sdcard上安装时signature可能获取的可能不是最旧的问题<br/>
461、LocaleManager适配<br/>


**2023年8月16号 至 2023年 9月5号 商业版代码更新内容**

460、14.0上JobScheduler适配<br/>
459、修复API broadcastIntentWithFeature<br/>
458、修复WhatsApp验证跳转的问题<br/>
457、内部Provider访问适配<br/>


**2023年8月2号 至 2023年 8月15号 商业版代码更新内容**

456、修复Twitter白屏的问题<br/>
455、修复ContentProvider在12.0+上的适配问题<br/>
454、修复微信在nova9z上崩溃的问题<br/>
453、修复微信等APP发送定位时黑屏的问题<br/>
452、编译SDK版本升级到14.0<br/>

**2023年7月13号 至 2023年 8月1号 商业版代码更新内容**

451、适配12.0+上蓝牙相关的10来个API<br/>
450、适配UserManager相关的10来个API<br/>


**2023年6月30号 至 2023年 7月12号 商业版代码更新内容**

449、修复JobService unbind崩溃问题<br/>
448、修复JobService persisted崩溃问题<br/>

**2023年5月26号 至 2023年 6月29号 商业版代码更新内容**

447、修复部分APP无法录音的问题<br/>
446、修复从Sdcard安装APK失败的问题<br/>
445、更改VA Demo包名<br/>

**2023年4月28号 至 2023年 5月25号 商业版代码更新内容**

444、适配Android 14.0<br/>

**2023年3月18号 至 2023年 4月27号 商业版代码更新内容**

443、修复GMS支持，修复各种crash，权限等问题<br/>
442、修复GooglePlay无法打开的问题<br/>
441、修复GooglePlay无法登录Google账号的问题<br/>
440、修复Youtube,WhatsApp等APP无法登录Google账号的问题<br/>
439、修复Facebook无法打开的问题<br/>


**2023年2月17号 至 2023年 3月17号 商业版代码更新内容**

438、修setPictureInPictureParams crash<br/>

**2023年1月27号 至 2023年 2月16号 商业版代码更新内容**

437、修复mOpPackageName空指针<br/>
436、修复13.0上PackageManager几个flags参数导致的crash<br/>
435、修复VAPP返回主页的Intent crash<br/>
434、TelecomManagerStub API适配<br/>
	
**2022年12月9号 至 2023年 1月26号 商业版代码更新内容**

433、修复PendingIntent flag处理问题<br/>

**2022年11月9号 至 2022年 12月8号 商业版代码更新内容**

432、修复Facebook在某些情况下无法启动的问题<br/>
431、启动外部App时，排除对VA自身的判断<br/>
430、修复queryIntentServices过滤规则<br/>


**2022年10月9号 至 2022年 11月8号 商业版代码更新内容**

429、修复当VA_AUTHORITY_PREFIX不等于包名时找不到Provider的问题："Failed to find provider info ..."<br/>
428、getPermissionActivityIntent处理<br/>
427、修复特殊情况下,检查权限无限弹窗<br/>
426、强调Intent使用外部通讯录(如果被第三方接管,建议使用外部可见)<br/>
425、新增几个Java API适配<br/>
424、修复修复部分后台Activity跳转问题<br/>
423、修复在10.0+上后台Activity无法启动的问题<br/>


**2022年 8月20号 至 2022年 10月8号 商业版代码更新内容**

422、新功能:增加启动插件Activity代理，绕过后台5s限制<br/>
421、修复Provider在10.0+上crash的问题<br/>
420、适配最新版微信<br/>
419、适配克隆时不显示sdcard上的APK<br/>
418、适配12.0+上PendingIntent Flags必须为FLAG_UPDATE_CURRENT/FLAG_IMMUTABLE<br/>
417、修复MediaProvider因为ACCESS_MEDIA_LOCATION 权限检查导致的crash<br/>
416、修复12.0+上debug模式下hook失败的问题<br/>
415、适配在Multi User账户下crash的问题<br/>
414、适配由于后台限制导致VA Core启动插件中Activity失败的问题<br/>


**2022年 7月27号 至 2022年 8月19号 商业版代码更新内容**

413、Android 13.0继续适配<br/>
412、主版调整为64bit<br/>
411、修复某些华为手机上App无法打开的问题<br/>
410、修复OPPO 13.0上无法打开应用的问题<br/>
409、修复百度语音TTS的调用问题<br/>
408、修复数据隔离后仍可以访问sdcard根目录的问题<br/>
407、修复鸿蒙手机上的崩溃问题<br/>
406、修复Debug模式下Hook失效问题<br/>
405、添加对BinderInvocationStub的混淆处理，避免混淆后崩溃问题<br/>
404、修复Native层调用free函数可能导致崩溃的问题<br/>
403、修复微信由于虚拟文件系统导致的崩溃问题<br/>


**2022年 7月9号 至 2022年 7月26号 商业版代码更新内容**

402、Android 13.0适配<br/>
401、修复开启虚拟储存后文件路径的处理问题<br/>
400、修复12.0上Notification没有提示的问题<br/>


**2022年 4月28号 至 2022年 5月31号 商业版代码更新内容**

399、修复onGetCallingUid hook引起的崩溃问题<br/>
398、修复微信8.0.22启动崩溃的问题<br/>


**2022年 4月5号 至 2022年 4月27号 商业版代码更新内容**

397、去掉sandhook中一些多余的hook代码，避免某些APP无法启动<br/>


**2022年 3月13号 至 2022年 4月5号 商业版代码更新内容**

396、新增功能:在VA中实现内置media provider，以支持媒体库隔离等需求<br/>
395、修复微信/QQ使用语音时崩溃<br/>
394、蓝牙崩溃问题适配<br/>
393、增加部分Log<br/>
392、删除一些无用代码<br/>


**2022年 1月22号 至 2022年 3月12号 商业版代码更新内容**

391、修复华为手机上StorageManager没有被hook的问题<br/>
390、修复最新版微信无法从SD卡启动的问题<br/>
389、PackageInfo中增加对requestedPermissionsFlags字段的处理<br/>
388、新增VSettingsProvider，避免内置应用没有权限操作settings provider导致异常<br/>
387、修复微信等APP启动黑屏，ANR等问题<br/>
386、新增对MediaProvider的hook<br/>
385、新增对插件shareUserId的处理，从而可以配置将插件的数据放到主包中<br/>
384、新增可以配置是否将Tinker禁用<br/>
383、修复Android 12权限处理适配<br/>


**2021年 12月30号 至 2022年 1月21号 商业版代码更新内容**

382、Sandhook适配12.0<br/>
381、修复Sandhook在部分11.0上不生效的问题<br/>
380、增加编译选项VA_FULL_EXT控制是否将VA源码编译到插件,满足加固场景。<br/>


**2021年 11月24号 至 2021年 12月29号 商业版代码更新内容**

379、Android 12.0第一轮适配已完成<br/>
378、Demo App在11.0上增加文件权限检测<br/>
377、修复静态广播接收者在独立进程无法接收广播的问题<br/>
376、修复微信第一次登录可能crash问题<br/>
375、修复部分APP无法显示头像问题<br/>
374、修复在部分OPPO手机上打不开问题<br/>


**2021年 9月21号 至 2021年 11月23号 商业版代码更新内容**

373、修复WhatsApp在360手机上黑屏问题<br/>
372、增加VA内外广播通信测试demo<br/>
371、修复抖音极速版兼容性问题<br/>
370、修复readlinkat返回值精度<br/>
369、修复从外部安装app,没有引用org.apache.http.legacy的问题<br/>
368、修复华为Nova 5i, 64位主包兼容性<br/>
367、修复11.0上外部存储重定向问题<br/>
366、修复11.0上GMS登录问题<br/>
365、修复11.0 部分APP读写sdcard报错的问题<br/>
364、修复va core进程死亡后，APP可能打不开的问题<br/>
363、增加未安装插件时无法启动的错误日志<br/>

**2021年 8月22号 至 2021年 9月20号 商业版代码更新内容**

362、横屏重新适配<br/>
361、修复部分APP通过file协议安装后无法打开的问题<br/>
360、修复传递给JobIntentService中Intent数据丢失问题<br/>
359、修复JobIntentService第二次调用无法工作的问题<br/>
358、修复华为手机上某些APP奔溃的问题<br/>
357、修复小米手机上游戏登录问题<br/>
356、修复某些应用加固后无法打开的问题<br/>
355、增加对关联启动权限检测<br/>
354、targetSdk 30适配<br/>
353、修复targetSdk为30时，某些应用无法上网的问题<br/>
352、修复targetSdk为30时，sdcard无法访问的问题<br/>
351、编译脚本中使用cmake替换gradle task<br/>
350、移除过时文档<br/>
	
**2021年 8月7号 至 2021年 8月21号 商业版代码更新内容**

349、调整优化gradle脚本<br/>
348、hidedenApiBypass支持Android R+<br/>
347、targetSdk 30 支持<br/>
346、修复VIVO系统服务bug<br/>
345、修复VIVO手机无法使用摄像头的bug<br/>
344、修复dex加载异常状态的获取<br/>
343、修复Android R上libart.so路径问题<br/>
342、修复Andoid Q+ 删除通知的bug<br/>
341、修复APN uri的权限检查<br/>
340、修复Android R暂停恢复线程状态<br/>
339、修复debug模式下部分hook失效情况<br/>
338、修复hook在R之后的一些bug<br/>

**2021年 4月25号 至 2021年 8月6号 商业版代码更新内容**

337、修复探探部分手机不能上传头像问题<br/>
336、修复Android 10 华为设备IO重定向问题<br/>
335、调整横竖屏逻辑,减少异常情况发生<br/>
334、添加Activity生命周期的回调接口<br/>
333、修复Android 12的广播问题<br/>
332、修复微信部分界面状态异常的BUG<br/>
331、修复Outlook、One drive、Teams、Zoom等海外app的支持<br/>
330、修复Android 11 一个权限请求BUG<br/>
329、修复部分cocos2d引擎只显示半屏的问题<br/>
328、修复微信在多用户下不能发送文件的问题<br/>
327、split apk 支持<br/>
326、Android S 支持<br/>

**2021年 2月24号 至 2021年 4月24号 商业版代码更新内容**

325、适配多用户环境<br/>
324、修复新版微信的兼容问题<br/>
323、兼容更多企业级加固<br/>
322、支持VAPP设置电源优化<br/>
321、修复缺失权限声明<br/>
320、修复Android 11上android.test.base库的引用<br/>
319、优化ext插件判断<br/>
318、优化安装时ABI的选择<br/>
317、修复Google文档在Android 11上崩溃的问题<br/>

**2020年 10月15号 至 2021年 2月23号 商业版代码更新内容**

316、解决新版爱加密、邦邦等加固的兼容性<br/>
315、修复WhatsApp不显示冷启动Splash的问题<br/>
314、优化对系统app的识别<br/>
313、完善多用户环境下的支持<br/>
312、解决ext插件部分情况下卡死的问题<br/>
311、支持Google Play在容器中下载APP<br/>
310、修复Android 11 QQ无法显示图片的问题<br/>
309、兼容Android 11运行Google Service<br/>
308、解决Android 11无法运行chromium<br/>
307、支持Hook @CriticalNative Method<br/>
306、修复JDK 13无法编译运行的问题<br/>
305、修复Service部分情况可能crash的问题<br/>
304、修复Android 11无法加载外部存储私有数据的问题<br/>
303、修复低版本app无法使用org.apache.http.legacy的问题<br/>
302、修复某些情况系统任务栈只显示最后一个的问题<br/>
301、完善不同平台的构建脚本<br/>
300、修复Android 11无法读取obb的问题<br/>
299、解决软件无法向后兼容的问题<br/>
298、重构VApp安装框架<br/>
297、重构virtual文件系统<br/>
296、修复某些情况下WebView无法启动的问题<br/>
295、修复VApp卸载重装的BUG<br/>
294、修复LOL手游的登录异常问题<br/>
293、支持安装Splits APK<br/>
292、支持动态配置主包环境<br/>
291、修复32位QQ调用64位微信卡顿的问题<br/>
290、修复Messenger调用Facebook崩溃的问题<br/>
289、优化对Google服务框架的支持<br/>
288、实现新的扩展包同步机制<br/>
287、修复Android 11正式版的异常问题<br/>
286、添加系统Package缓存，优化性能<br/>
285、修复disabled组件还能被PMS查询的BUG<br/>
284、修复微信部分界面Launch行为异常的问题<br/>
283、修复ContentProvider.getCallingPackage返回Host包名的BUG<br/>
282、修复uid虚拟化的BUG，解决部分app权限检查失败的问题<br/>
281、重写PendingIntent, IntentSender的实现<br/>
280、优化进程管理，修复长期存在的概率性进程死锁问题<br/>
279、重写Service实现，Service生命周期更准确，不容易被杀死<br/>


**2020年 9月13号 至 2020年 10月15号 商业版代码更新内容**

278、修复 64 位 App 无法调用 32 位 App 的问题<br/>
277、修复 Android R 加载 HttpClient 的问题 <br/>
276、修复 Android R debug 模式下的崩溃问题<br/>

**2020年 8月23号 至 2020年 9月12号 商业版代码更新内容**

275、添加缺失的 service hook<br/>
274、修复百度翻译无法启动的问题 <br/>
273、修复 GP 下载的 split app 无法启动的问题<br/>

**2020年 7月10号 至 2020年 8月22号 商业版代码更新内容**

272、修复 Service 创建<br/>
271、添加 NotificationService 缺失的 Hook<br/>
270、修复 Yotube 崩溃<br/>

**2020年 5月19号 至 2020年 7月9号 商业版代码更新内容**

269、初步适配 Android 11 beta1<br/>
268、修复小红书多开闪退的问题<br/>
267、修复某些 App 多开报“应用签名被篡改”的问题<br/>

**2020年 4月24号 至 2020年 5月18号 商业版代码更新内容**

266、修复 sh 调用错误<br/>
265、修复 9.0 以上最新版 Facebook 无法登陆的问题<br/>
264、帮助企业微信修复启动虚拟存储的情况下无法拍照的问题<br/>
263、修复某些情况下 64位 app 打不开 Activity 的问题<br/>

**2020年 3月24号 至 2020年 4月23号 商业版代码更新内容**

262、修复 Vivo 设备提示安装游戏 SDK 的问题<br/>
261、修复 Android Q 无法加载部分系统 so 的问题<br/>
260、修复华为设备微博未响应<br/>
259、忽略不必要的权限检查造成的崩溃<br/>
258、修复 WPS 分享文件崩溃的问题<br/>
257、部分 10.0 设备的闪退问题<br/>

**2020年 3月7号 至 2020年 3月23号 商业版代码更新内容**

256、修复微信同时打开两个页面问题<br/>
255、修复微信登陆成功但是返回登陆页面的问题<br/>
254、修复最新版 QQ 无法下载附件的问题<br/>
253、更新 SandHook 版本<br/>
252、修复 9.0 以上安装未签名Apk问题 <br/>
251、修复 10.0 的定位问题<br/>

**2020年 1月16号 至 2020年 3月6号 商业版代码更新内容**

250、调整 lib 重定向逻辑<br/>
249、修复三星 10.0 系统上的崩溃问题<br/>
248、修复 release build 的 hook 异常<br/>
247、增加 SandHook 的 proguard 规则<br/>
246、修复对部分 App 中 VirtualApk 的兼容问题 <br/>
245、修复 VA 内部请求安装 apk 失败的问题<br/>

**2019年 12月26号 至 2020年 1月15号 商业版代码更新内容**

244、修复 Android Q 遗漏的 hook<br/>
243、禁用 Emui10 的 AutoFill<br/>
242、增加新 api 结束所有 activity<br/>

**2019年 12月15号 至 2019年 12月25号 商业版代码更新内容**

241、修复 Emui10 上企业微信等 App 无法启动的问题<br/>
240、修复在 4.x 可能导致的崩溃<br/>
239、升级 SandHook 修复对 Thread 类的 Hook<br/>
238、修复 Android Q 某些接口导致的权限问题<br/>

**2019年 11月20号 至 2019年 12月14号 商业版代码更新内容**

237、修复 Notification 缓存导致的崩溃<br/>
236、修复高版本 Notification 的 classloader 问题<br/>

**2019年 11月9号 至 2019年 11月19号 商业版代码更新内容**

235、修复 Android 5.x 的 ART Hook <br/>
234、修复 ART Hook 可能导致的死锁问题 <br/>

**2019年 11月2号 至 2019年 11月8号 商业版代码更新内容**

233、修复 WPS, 网易邮箱等在 Q 设备上崩溃的问题 <br/>
232、修复汤姆猫跑酷在部分 Q 设备上崩溃的问题 <br/>
231、修复 QQ 在部分 Q 设备上崩溃的问题 <br/>

**2019年 10月25号 至 2019年 11月1号 商业版代码更新内容**

230、修复克隆 Google Play 下载的 64位 App<br/>
229、修复企业微信 <br/>
228、修复 Telegram <br/>

**2019年 10月8号 至 2019年 10月24号 商业版代码更新内容**

227、修复 Android P 下 AppOspManager 的异常 <br/>
226、添加 Android P 下 ActivityTaskManager 丢失的 Hook <br/>
225、修复 Android P 下 Activity Top Resume 异常 <br/>
224、支持在系统多用户模式下运行! <br/>

**2019年 10月8号 商业版代码更新内容**

223、修复Android P 以上内部 app 返回桌面异常的问题 <br/>
222、64位分支支持 Android Q <br/>

**2019年 9月20号 至 2019年 10月7号 商业版代码更新内容**

221、修复安装在扩展插件中的 apk 无法正确显示图标和名称的问题 <br/>
220、修复 twitter 无法打开的问题 <br/>
219、正式兼容 Android Q 正式版! <br/>
218、修复 Android Q 某些 Activity 无法再次打开的问题 <br/>
217、初步适配 Android Q 正式版 <br/>
216、修复数个64位分支的 Bug <br/>
215、新增加支持32位插件的64位分支，该分支支持32位旧设备并且64位设备在32位插件的情况下可以支持32位旧应用 <br/>

**2017年 12月 至 2019年 7月 30 日 商业版代码更新内容**

214、改进 App 层提示信息 <br/>
213、改进部分编码 <br/>
212、修复从宿主向插件发送广播的方法 <br/>
211、兼容最新版 gradle 插件 <br/>
210、增加广播命名空间以避免多个使用 VA 技术的 App 互相干扰 <br/>
209、修复 IMO 打不开的问题 <br/>
208、修复部分 ContentProvider 找不到的问题 <br/>
207、支持纯32位模式，以兼容老设备 <br/>
206、初步支持纯64位模式，以应对8月份的谷歌市场的策略变化 <br/>
205、适配到 Android Q beta4 <br/>
204、修复了货拉拉无法安装的问题<br/>
203、优化了64位apk的判定逻辑<br/>
202、修复配置网络证书的 App 的联网<br/>
201、重构组件状态管理<br/>
200、优化 MIUI/EMUI ContentProvider 兼容性<br/>
199、修复 StorageStats Hook<br/>
198、修复快手无法登陆<br/>
197、修复 YY 无法启动，更好的兼容插件化框架<br/>
196、修复 Facebook 登陆<br/>
195、修复 Google Play 下载的 App 无法找到 so 的问题(皇室战争)<br/>
194、修复 split apk 支持<br/>
193、修复 Youtube 无法启动<br/>
192、修复优酷无法启动的问题<br/>
191、修复多开时app间可能存在广播namespace冲突的BUG<br/>
190、采用新的策略绕过Android P以后的Hidden Policy API<br/>
189、适配Android Q(beta1)<br/>
188、修复华为设备部分app无法识别存储的问题<br/>
187、修复启动进程可能失败导致app无法运行的问题<br/>
186、修复4.4设备部分native符号无法找到的问题<br/>
185、修复部分设备WebView包名获取失败的问题<br/>
184、修复Service细节处理的问题<br/>
183、优化启动速度<br/>
182、修复WebView在少数机型加载失败的情况<br/>
181、修复Lib决策的问题<br/>
180、修复部分华为机型无法读取内存卡的问题<br/>
179、修复Service可能存在的问题<br/>
178、允许根据intent判断Activity是否在外部启动<br/>
177、修复部分机型上Gms和Google Play启动到了不正确的环境<br/>
176、修复新实现的StaticBroadcast导致的兼容性问题<br/>
175、修复Android P上无法使用apache.http.legacy的问题<br/>
174、实现Native trace<br/>
173、优化IO Redirect性能<br/>
172、修复wechat部分时候出现网络无法连接的问题<br/>
171、修复小概率process attach不正确的BUG<br/>
170、开始下一阶段的ROADMAP<br/>
169、解决Android P无法注册超过1000个广播导致的问题<br/>
168、修复可能导致ANR的DeadLock<br/>
167、修复部分app动态加载so失败的问题<br/>
166、修复免安装运行环境下部分机型第一次打开出现黑屏的问题<br/>
165、兼容适配多款主流的Android模拟器<br/>
164、优化启动性能<br/>
163、解决多个内存泄露问题<br/>
162、修复IO Redirect优先级的问题<br/>
161、修复8.0以下设备Messenger无网络连接的问题<br/>
160、修复双开时外部app卸载时内部app仍然保留的BUG<br/>
159、修复部分腾讯加固无法运行的问题<br/>
158、修复Instagram无法登录Facebook的BUG<br/>
157、修复进程小概率可能重复启动的BUG<br/>
156、修复GET_PERMISSIONS没有获取权限的BUG<br/>
155、修复startActivityIntentSender的BUG<br/>
154、修复vivo设备部分Activity无法启动的问题<br/>
153、修复app无法调用外部app选择文件的问题<br/>
152、完善Android P的兼容<br/>
151、兼容Android P的Google服务<br/>
150、解决Messenger部分功能异常<br/>
149、完善IO Redirect<br/>
148、大量适配Gms, 修复Gms运行过程中进程无限重启的问题<br/>
147、重新实现Service的运行机制<br/>
146、完善64bit，提供了部分ROM配置64bit Engine权限的API<br/>
145、修复了4.4设备上的Activity启动问题<br/>
144、支持excludeFromRecent属性<br/>
143、修复Instagram无法Facebook登录的问题<br/>
142、修复Facebook第一次登录闪退的问题<br/>
141、支持以64位模式运行Gms、Google play、Play game<br/>
140、支持在双开/免安装运行的Google play中下载和安装app<br/>
139、修复DownloadManager的BUG<br/>
138、修复Google play返回上层时重启界面的BUG<br/>
137、修复免安装模式下so决策问题<br/>
136、优化构建脚本，便于引入项目<br/>
135、修复移动MM SDK无法启动的问题<br/>
134、修复微信摇一摇的BUG<br/>
133、修复中兴设备不稳定的BUG<br/>
132、支持ARM64下的IO Redirect<br/>
131、修复USE_OUTSIDE模式下外部app更新时，内部app没有更新的BUG<br/>
130、兼容最新Android 9.0(代号: pie) 及正式版之前发布的四个Preview版本<br/>
129、兼容内置houdini的x86设备<br/>
128、WindowPreview技术，使app启动与真实app达到一样的速度<br/>
127、新的ActivityStack以提高app运行质量<br/>
126、解决接入Atlas Framework的app运行异常的问题<br/>
125、现在可以定义虚拟app返回桌面的具体行为<br/>
124、现在双开模式下app随系统动态更新，不需要手动检查<br/>
123、支持targetSdkVersion >= 26时仍可正常运行低版本的app<br/>
122、兼容腾讯游戏管家的QDroid虚拟引擎 (beta)<br/>
121、大量重构底层代码，大幅提升运行速度<br/>
120、修复网易新闻分享到微博后无法取消的问题<br/>
119、修复App自定义权限无法识别的问题<br/>
118、修复墨迹天气app无法启动的问题<br/>
117、修复部分政府app无法启动的问题<br/>
116、API的变动详见代码<br/>
115、修复三星系列应用的相互调用问题<br/>
114、修复小米应用在非小米系统的账号问题<br/>
113、修复分享/发送等第三方调用，返回页面不正常<br/>
112、修复应用宝提示不能安装<br/>
111、调用第三方app，对uri进行加密<br/>
110、适配前刘海<br/>
109、适配小米rom的hook<br/>
108、适配努比亚录音问题<br/>
107、内部悬浮窗权限控制<br/>
106、优化自定义通知栏的处理<br/>
105、修复Context的INCLUDE_CODE权限问题<br/>
104、适配华为，oppo的角标<br/>
103、修复百度视频的进程重启问题<br/>
102、修复某些snapchat的无法启动问题<br/>
101、适配autofill服务，例如piexl系列<br/>
100、完善64位的io hook<br/>
99、优化hook库的兼容性，加回dlopen<br/>
98、64位扩展包的so移到32位主包。（jni代码改动后，在Run之前，请先build一次）<br/>
97、通知栏改动：适配8.1的通知渠道；移除应用时，移除应用的全部通知<br/>
96、兼容部分app，需要设置android:largeHeap=true<br/>
95、修复ffmpeg库的视频无法播放问题<br/>
94、优化横竖屏切换<br/>
93、降低通过Intent.ACTION_VIEW调用外部Activity限制。<br/>
92、兼容MG SDK<br/>
91、64位支持还在开发阶段<br/>
90、更新混淆配置app/proguard-rules.pro，必须加规则-dontshrink<br/>
89、优化模拟机型，例如：模拟后，某些app不出现设备验证<br/>
88、提高dex2oat兼容性<br/>
87、优化模拟定位<br/>
86、移除dlopen<br/>
85、targetVersion可以改为26：支持targetVersion<23的app动态权限申请，支持targetVersion<24的文件Uri<br/>
84、installPackage改为默认异步形式<br/>
83、为了支持64位模式，换回aidl<br/>
82、去掉SettingHandler现在可以动态设置特殊规则，规则会存储，不需要重复设置<br/>
81、增加2个native_setup<br/>
80、提高jobService兼容性<br/>
79、ShortcutService相关：关联VASettings.ENABLE_INNER_SHORTCUT<br/>
78、为了稳定性和运行效率，去掉上个版本的蓝牙，wifi，不声明权限的适配。<br/>
77、增加app启动异常的广播Constants.ACTION_PROCESS_ERROR<br/>
76、修复少数游戏横屏判断问题<br/>
75、demo增加机型模拟<br/>
74、适配vivo一个自定义权限（后台弹窗）VA是把一个历史acitivty返回前台，vivo需要这个权限。<br/>
73、如果没有蓝牙权限，返回默认值（海外用）<br/>
72、修复uid权限检查问题<br/>
71、安全性更新，内部应用的文件权限控制<br/>
70、提高内部app调用的兼容性，第三方登录，分享<br/>
69、自动过滤没权限的外部ContentProvider<br/>
68、增加功能：内部app的权限检查（默认关闭）<br/>
67、机型模拟:Build类和build.prop<br/>
66、提高对乐固加固的app兼容性<br/>
65、适配三星wifimanager<br/>
64、修复ipc框架一个参数传递问题（IPCMethod这个类必须更新）<br/>
63、补全7.0通知栏的hook<br/>
62、修正8.0动态快捷菜单的hook<br/>
61、SettingHandler新增一个适配接口，主要适配各种游戏<br/>
60、功能改动：google自动安装改为手动安装，避免第一次启动时间过久<br/>
59、可以禁止访问外部某个ContentProvider<br/>
58、适配华为桌面图标数量<br/>
57、权限分类注释，标注可删除权限。<br/>
56、增加双开模式的app跟随外部升级的开关。<br/>
55、提高app的jni兼容性。<br/>
54、提高对app集成其他插件框架的兼容性。<br/>
53、增加设置接口，根据包名进行设置。<br/>
52、增加Uri的适配范围，支持通过Uri分享和查看文件。<br/>
51、修复一个在三星8.0的问题。<br/>
50、提高对系统自带的app组件兼容性，更好兼容chrome webview，google service。<br/>
49、提高ART稳定性<br/>
48、增加相机适配范围<br/>
47、支持内部App在8.0下的快捷方式管理<br/>
46、修复exec异常<br/>
45、提高稳定性（修复微信登录闪退）<br/>
44、解决微信数据库崩溃问题<br/>
43、修复部分4.4设备崩溃问题<br/>
42、修复后台应用易被杀死，土豆视频黑屏，新浪微博无法打开，优酷两次返回无法退出。<br/>
41、增加应用的保活机制，双开APP更不易被杀死。<br/>
40、优化虚拟引擎启动性能。<br/>
39、兼容了大部分的加固，第三方APP兼容性对比上一版提升40%+。<br/>
38、修复某些rom下，快捷方式图标不正确<br/>
37、兼容以前组件StubFileProvider<br/>
36、适配部分新ROM的虚拟IMEI<br/>
35、改善进程初始化代码，增加稳定性<br/>
34、添加内部发送Intent.ACTION_BOOT_COMPLETED的广播，可以设置开关<br/>
33、适配关联google play游戏，支持游戏使用google登录<br/>
32、适配android O的google service框架<br/>
31、适配android O 快捷方式<br/>
30、适配耳机模式<br/>
29、某些rom对intent的大小限制，demo添加缩放快捷方式图标代码<br/>
28、修复多开情况下一个bug<br/>
27、修复某些情况下MediaController的bug<br/>
26、修复4.1.2的StubFileProvider报错<br/>
25、分享的uri处理<br/>
24、修复跨app调用Activity的回调<br/>
23、前台服务的通知栏拦截开关<br/>
22、附带doc<br/>
21、完善VA内部的intent的CHOOSE回调<br/>
20、Android O的通知栏适配2<br/>
19、ipc框架优化, 提高判断binder的存活准确性<br/>
18、jni的log开关 Android.mk:LOCAL_CFLAGS += -DLOG_ENABLE<br/>
17、混淆配置<br/>
16、Android O的通知栏适配<br/>
15、修复部分app网络卡的问题<br/>
14、适配 android 8.0的dl_open（jni加载）<br/>
13、修复华为emui8.0的一个bug<br/>
12、完善定位<br/>
11、设置手机信息，imei伪装算法<br/>
10、适配8.0某个功能（主要app：whatsapp）<br/>
9、修复内部微信等应用，无法更新图片，视频<br/>
8、demo增加安装监听，自动升级克隆模式的应用<br/>
7、7.0的file provider适配<br/>
6、增加了定位代码<br/>
5、代码进行了架构优化<br/>
4、与开源版不同的特征<br/>
3、解决了微信被封的一些问题<br/>
2、修复了部分机型兼容性<br/>
1、修复了12个小BUG<br/>
</details>






