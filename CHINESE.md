[![VA banner](https://raw.githubusercontent.com/asLody/VirtualApp/master/Logo.png)](https://github.com/asLody/VirtualApp)

项目简介
---
VirtualApp是由`罗盒科技`开发并运营的商业项目，VirtualApp在你的App内创建一个`虚拟空间`，你可以在虚拟空间内任意的`安装`、`启动`和`卸载`APK，这一切都与外部隔离，如同一个`沙盒`。

运行在`VA`中的APK无需在外部安装，即VA支持**免安装运行APK**。

VA目前被广泛应用于`插件化开发、无感知热更新、云控自动化、多开、手游租号、手游手柄免激活、区块链、移动办公安全、军队政府保密、手机模拟信息、脚本自动化、自动化测试`等技术领域，但它决不仅限于此，Android本身就是一个极其开放的平台，免安装运行APK这一Feature打开了无限可能-----这都取决于您的想象力。

**Github上代码**已在2017年12月份**停止更新**，商业版代码在持续更新中，并有以下提升：

1. 兼容最新Android Q
2. 不易被杀毒软件误报
3. 框架优化，性能大幅提升
4. 手机系统及APP兼容性大幅提升
5. 完美运行Google服务
6. 支持运行纯64位App
7. 支持Dalvik和Art的Java Hook(API同Xposed)
8. 增加定位模拟代码
9. 增加改机代码
10. 其他180多项问题的修复和改进，详情请见下表


授权说明
------

罗盒科技在深圳、上海及山东设有公司，于2015年至2018年申请多项VirtualApp知识产权，`受中华人民共和国知识产权法保护`。当您需要使用Github上的VirtualApp代码时，**请购买商业授权**，获取商业授权后将可以收到我们提供的VirtualApp商业版全部源代码。数十家授权客户在付费使用VirtualApp商业版代码，集成VirtualApp代码的APP日启动次数超过2亿次，数千位安卓工程师向我们提供不同场景下的用户反馈，通过我们技术团队的不断努力优化，VirtualApp商业版代码已经日益完善。`您获取授权后，将成为其中一员，享受这些不断迭代完善后的成果。并可以和我们的授权客户进行运营、技术及商业上的互动合作。`


<br/>
负责人：张总<br/>
手机：130-321-77777<br/>
QQ/微信：10890<br/>
<br/>

严重声明
------

您如果未经授权将VirtualApp用于**内部使用、商业牟利或上传应用市场**，我们发现后将直接取证后报警（侵犯著作权罪）或起诉，这将对您所属公司造成刑事责任及法律诉讼，影响到您公司的商誉和投资。`购买商业授权为您节省大量开发、测试和完善时间，让您有更多时间用于创新及盈利`。<br/>

2019年3月1日起，我们将VirtualApp商业版代码更新方式升级为`GitHub私有库`方式，授权后即可加入。在授权期内可以和众多授权项目近2100多位安卓工程师一同迭代升级，反馈BUG，我们会第一时间更新解决。

**

**2019年 3月15号 最新一期更新内容**
189、修复多开时app间可能存在广播namespace冲突的BUG<br/>
190、采用新的策略绕过Android P以后的Hidden Policy API<br/>
191、适配Android Q(beta1)<br/>


**2018年 1月1日 至 2019年 1月1日 商业版代码更新内容**
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

