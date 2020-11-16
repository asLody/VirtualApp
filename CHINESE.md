[![VA banner](https://raw.githubusercontent.com/asLody/VirtualApp/master/Logo.png)](https://github.com/asLody/VirtualApp)

项目简介
---
VirtualApp(以下简称：VA)是一款运行于Android系统的沙盒产品，可以理解为轻量级的“Android虚拟机”。其产品形态为高可扩展，可定制的集成SDK，您可以基于VA或者使用VA定制开发各种看似不可能完成的项目。VA目前被广泛应用于插件化开发、无感知热更新、云控自动化、多开、手游租号、手游手柄免激活、区块链、移动办公安全、军队政府保密、手机模拟信息、脚本自动化、自动化测试等技术领域。

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
支持4.1-11.0，支持32位/64位app，支持ARM以及X86处理器。并且支持未来将更新的Android版本。

- 易扩展与集成<br/>
VA 的集成方式与普通Android库类似，即使您的App已经完成上线，您也方便的可以集成VA，享受VA带来的能力。

- 支持Google服务<br/>
提供Google服务的支持，以支持海外的App

VA实例
---

- APP多开<br/>
游戏多开，微信多开等

- 虚拟手机<br/>
在已有的手机上虚拟出一部专用手机，企业单位可以进行监控管理

- 安全空间<br/>
独立空间，内外隔离，保证内部的私密与安全

- 游戏租号<br/>
一键上号，一键登陆，免去用户频繁输入账号密码

- Xposed<br/>
免Root实现Xposed环境

- 谷歌空间<br/>
免去国产手机无法使用谷歌服务的烦恼，一键安装使用谷歌应用

- 游戏手柄<br/>
免Root方便的使用手柄映射

- 游戏中心<br/>
免安装运行游戏，多开游戏，游戏内屏录制，统一的游戏更新管理，投射游戏画面到电视。

- 游戏自动化<br/>
方便地模拟用户操作

- 军政安全<br/>
文件隔离/组件隔离/进程通讯隔离/加密监控等等



**现在开始使用VA发挥你的想象力吧！**


**Github上代码**已在2017年12月份**停止更新**，商业版代码在持续更新中，并有以下提升：

1. 兼容最新Android R
2. 不易被杀毒软件误报
3. 框架优化，性能大幅提升
4. 手机系统及APP兼容性大幅提升
5. 完美运行Google服务
6. 支持运行纯64位App
7. 内置`XPosed Hook`框架
8. 增加定位模拟代码
9. 增加改机代码
10. 其他近300项问题的修复和改进，详情请见下表


授权说明
------

罗盒科技在上海及山东济宁设有公司，于2015年至2018年申请多项VirtualApp知识产权，`受中华人民共和国知识产权法保护`。当您需要使用Github上的VirtualApp代码时，**请购买商业授权**，获取商业授权后将可以收到我们提供的VirtualApp商业版全部源代码。数十家授权客户在付费使用VirtualApp商业版代码，集成VirtualApp代码的APP日启动次数超过2亿次，数千位安卓工程师向我们提供不同场景下的用户反馈，通过我们技术团队的不断努力优化，VirtualApp商业版代码已经日益完善。`您获取授权后，将成为其中一员，享受这些不断迭代完善后的成果。并可以和我们的授权客户进行运营、技术及商业上的互动合作。`


<br/>
负责人：张总 <br/>
手机：130-321-77777 <br/>
微信：10890 <br/>
<br/>


严重声明
------

您如果未经授权将VirtualApp用于**内部使用、商业牟利或上传应用市场**，我们发现后将直接取证后报警（侵犯著作权罪）或起诉，这将对您所属公司造成刑事责任及法律诉讼，影响到您公司的商誉和投资。`购买商业授权为您节省大量开发、测试和完善时间，让您有更多时间用于创新及盈利`。<br/>

2019年3月1日起，我们将VirtualApp商业版代码更新方式升级为`GitHub私有库`方式，授权后即可加入。在授权期内可以和众多授权项目近2100多位安卓工程师一同迭代升级，反馈BUG，我们会第一时间更新解决。

**2020年 10月15号 至 2020年 11月16号 商业版代码更新内容**

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

<details>
<summary>2018年 1月1日 至 2019年 7月 30 日 商业版代码更新内容(下拉打开)</summary>
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


