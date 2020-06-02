[![VA banner](https://raw.githubusercontent.com/asLody/VirtualApp/master/Logo.png)](https://github.com/asLody/VirtualApp)

[中文版](CHINESE.md "中文")

Project Profile
---------------

VirtualApp is a commercial project developed and operated by Luohe technology, which creates a virtual space within your App. You can `install, start and uninstall APK arbitrarily in the virtual space`, which is isolated from the outside, like a *sandbox*. 
APK running in VA does not need to be `installed externally`, that is, the VA supports running APK without installation.
VA is currently widely used in plug-in development, non-thermal update, cloud control automation, multi-open, mobile game rent number, mobile game handle activation free, block chain, mobile office security, military and government secrecy, mobile phone simulation information, script automation, automated testing and other technical fields, but it is not limited to this. Android itself is an extremely open platform, `and the ability to run APK without installing it opens up unlimited possibilities -- which depend on your imagination.`
The code on Github has been updated since December 2017. The commercial version of the code is still being updated, with the following improvements:

1. New Code Characteristics
2. Framework optimization and performance improvement
3. Compatibility of mobile phone system and APP has been greatly improved
4. Run Google services perfectly
5. Compatibility with the latest Android Q (10.0)
6. Support running 64-bit App
7. Java Hook that supports Dalvik and Art (API and Xposed)
8. Add positioning simulation code
9. Add the machine changing code
10. Please refer to the table below for details of the repair and improvement of more than 180 other problems


Authorization Note
------------------

Luohe Technology has set up companies in Shanghai, Shenzhen and Shandong. From 2015 to 2018, it applied for many VirtualApp intellectual property rights, which are protected by the Intellectual Property Law of the People's Republic of China. When you need to use the VirtualApp code on Github, please purchase the commercial license. After obtaining the commercial license, you will receive the full source code of the commercial version of VirtualApp provided by us. Dozens of authorized customers pay for the use of VirtualApp commercial code, and the number of APPs that integrate VirtualApp code starts more than 200 million times a day. Thousands of Android engineers provide us with user feedback in different scenarios, and VirtualApp commercial version code has become increasingly sophisticated through the continuous efforts of our technical team to optimize. After you get the authorization, you will become one of them and enjoy the fruits of continuous iteration and improvement. And we can cooperate with our authorized customers in operation, technology and business. 


Person in charge: General Zhang<br/>
Mobile: +86 1303217777 7<br/>
Wechat: 10890<br/>
Company email: zl@aluohe.com


Strictness Disclaimer
---------------------

If you use VirtualApp for internal use, commercial profit or upload to the application market without authorization, we will directly collect evidence and report to the police (copyright infringement) or prosecute, which will cause criminal liability and legal proceedings to your company and affect your company's goodwill and investment. Buying a commercial license saves you a lot of development, testing, and refining time, allowing you more time to innovate and monetize. 



**The updating contents of commercial version code from December 13, 2018 to June 13, 2019**

206. Preliminary support for pure 64-bit mode to meet the requirements of the Google Market<br/>
205. Preliminary support for Android Q beta4<br/>
204. Fix install of Huolala<br/>
203. Fix check logic for 64bit apk<br/>
202. Support NetworkSecurityConfig<br/>
201. Rewrite Component State Manager<br/>
200. Optimaize compatibility of MIUI/EMUI ContentProvider<br/>
199. Fix StorageStats Hook<br/>
198. Fix login of Kuaishou<br/>
197. Fix support for some plugin frameworks(Small, Atlas)<br/>
196. Fix login of Facebook<br/>
195. Fix so load error of some app download from Google Play(COC)<br/>
194. Fix suppport for split apk<br/>
193. Fix crash of Youtube<br/>
192. Fix crash of Youku<br/>
191. Fix conflict of broadcast namespace<br/>
190. Add a new policy to bypass Hidden Policy API above Android P<br/>
189. Pre support for Android Q(beta1)<br/>
188. Fix access to SD Card in some huawei devices<br/>
187. Fix some launch process failure<br/>
186. Fix get native symbol failure in some 4.4 devices<br/>
185. Fix got package name failure by WebView in some devices<br/>
184. Fix detail of service<br/>
183. Optimize launch speed<br/>

The updating contents of commercial version code from January 1, 2018 to December 13, 2018.

182. Fix WebView loading failure in a few models<br/>
181. Fix Lib decision problems<br/>
180. Fix the problem that some Huawei models could not read memory CARDS<br/>
179. Fix possible problems with the Service<br/>
178. Allow judging whether an Activity is started externally based on intent<br/>
177. Fix incorrect environment boot of Gms and Google Play on some models<br/>
176. Fix compatibility problems caused by the StaticBroadcast new implementation<br/>
175. Fix the problem that apache. HTTP. Legacy cannot be used on Android P<br/>
174. Implement Native trace<br/>
173. Optimize IO Redirect performance<br/>
172. Fix the occasional network failure of wechat<br/>
171. Fix bugs with incorrect small probability process attach<br/>
170. ROADMAP for the next stage<br/>
169. Solve the problem caused by Android P's inability to register more than 1000 broadcasts<br/>
168. Fix DeadLock that may cause ANR<br/>
167. Fix the problem of partial app dynamic loading failure<br/>
166. Fix the problem that black screen appears when some models are opened for the first time under the operating environment without installation<br/>
165. Compatible with many mainstream Android emulators<br/>
164. Optimize start-up performance<br/>
163. Resolve multiple memory leaks<br/>
162. Fix IO Redirect priority issues<br/>
161. Fix the problem of no network connection for Messenger device below 8.0<br/>
160. Fix Bug that remained in the internal app when the external app was uninstalled in dual-open mode<br/>
159. Fix the problem of some tencent reinforcement can not run<br/>
158. Fix the BUG that Instagram could not log into Facebook<br/>
157. Fix Bug that may be started repeatedly with low probability<br/>
156. Fix Bug of the GET_PERMISSIONS without authorization<br/>
155. Fix Bug of starting Activity Intent Sender<br/>
154. Fix the problem that activity on some vivo devices could not be started<br/>
153. Fix the problem that the app could not call the external app to select the file<br/>
152. Improve the compatibility of Android P<br/>
151. Google service compatible with Android P<br/>
150. Solve some abnormal functions of Messenger<br/>
149. Improve IO Redirect<br/>
148. A large number of Gms are adapted to fix the problem of unlimited process restart during Gms operation<br/>
147. Re-implement the operation mechanism of Service<br/>
146. Improve 64 bit and provide API for some ROM configuration 64bit Engine permissions<br/>
145. Fix Activity startup problem on 4.4 device<br/>
144. Support the exclude From Recent attribute<br/>
143. Fix the problem that Instagram could not log into Facebook<br/>
142. Fix the problem of Facebook's first login flashback <br/>
141. Support to run Gms, Google play and play game in 64-bit mode<br/>
140. Support downloading and installing apps in Google Play with dual-open/install-free running<br/>
139. Fix Bug of Download Manager<br/>
138. Fix the BUG of restarting the interface when Google play returned to the upper layer<br/>
137. Fix the so decision-making problem in the installation-free mode<br/>
136. Optimize the build script to facilitate the introduction of projects<br/>
135. Fix the problem that the mobile MM SDK could not be started<br/>
134. Fix the BUG of WeChat shake<br/>
133. Fix the unstable BUG of ZTE equipment<br/>
132. Support ARM64 IO Redirect<br/>
131. Fix the BUG that no updates are made to the internal app when the external app is updated in USE_OUTSIDE mode<br/>
130. Compatible with the latest version of Android 9.0(code-named pie) and the four Preview versions released before the official version<br/>
129. Compatible with x86 devices with built-in Houdini<br/>
128. Windows preview technology enables App to start at the same speed as real App<br/>
127. New Activity Stack to improve running quality of APP<br/>
126. Solve the abnormal operation of App connected to Atlas Framework<br/>
125. Be able to define the specific behavior of the virtual app returning to the desktop<br/>
124. Now the app is dynamically updated with the system in double-open mode, without manual check<br/>
123. When target Sdk Version >= 26, the lower version of the app can still run normally<br/>
122. QDroid virtual engine (beta) compatible with tencent game manager<br/>
121. Refactor a lot of underlying code to greatly improve the running speed<br/>
120. Fix the problem that netease news cannot be cancelled after sharing on weibo<br/>
119. Fix the problem that App custom permissions cannot be recognized<br/>
118. Fix the problem that the app could not be started due to ink stains<br/>
117. Fix the problem that some government apps could not be started<br/>
116. See the code for changes to the API<br/>
115. Fixed the mutual call problem of Samsung series of applications<br/>
114. Fix the account problems of Xiaomi applications in non-xiaomi systems<br/>
113. Repair third-party calls such as share/send, and the returned page is abnormal<br/>
112. Fix the problem that App prompt cannot be installed<br/>
111. Use the third-party App and encrypt the uri<br/>
110. Adapt to the bangs<br/>
109. Adapt to the hook of Xiaomi ROM<br/>
108. Adapt to the problem of Nubian recording<br/>
107. Permission control of internal suspension window<br/>
106. Optimize the processing of customized notification bar<br/>
105. Fix the INCLUDE_CODE permission problem of Context<br/>
104. Adapt to corner marks of Huawei and Oppo<br/>
103. Fix the problem of Baidu video process restart<br/>
102. Fix some snap chat start-up problems<br/>
101. Adapt to autofill service, such as piexl series<br/>
100. Improve 64-bit IO hook<br/>
99. Optimize hook library compatibility, add back dlopen<br/>
98. The so of the 64-bit extension package is moved to the 32-bit master package. (after jni code changes, please build it once before running)<br/>
Notification Bar Change: Adapt 8.1 Notification Channel; Remove all notifications of the application when the application is removed<br/>
96. Compatible with some Apps, need to set android:largeHeap=true<br/>
95. Fix the problem of ffmpeg library video can not play<br/>
94. Optimize horizontal and vertical screen switching<br/>
93. Reduce the limit of calling external activities with intent.action_view.<br/>
92. Compatible with MG SDK<br/>
91. 64-bit support is still in development<br/>
90. Update the confusion configuration app/proguard-rules.pro, must add the rule -dontshrink<br/>
89. Optimize the simulation model, for example: after simulation, device verification does not appear in some Apps<br/>
88. Improve dex2oat compatibility<br/>
87. Optimize simulation positioning<br/>
86. Remove dlopen<br/>
85. TargetVersion can be changed to 26: support dynamic permission application for app with targetVersion < 23 and file Uri with targetVersion < 24<br/>
84. Install Package changed to the default asynchronous form<br/>
83. Support 64-bit mode and switch back to aidl<br/>
82. Remove Setting Handler can now dynamically set special rules, which are stored and do not need to be set repeatedly<br/>
81. Add 2 native_setup<br/>
80. Improve jobService compatibility<br/>
79. Shortcut Service: Associate VASettings.ENABLE_INNER_SHORTCUT<br/>
78. For the sake of stability and operational efficiency, remove the previous version of bluetooth and wifi, and do not declare the adaption of permissions.<br/>
77. Increase the broadcast Constants of app startup exceptions with ACTION_PROCESS_ERROR<br/>
76. Fix a few game screen judgment problems<br/>
75. Demo adds model simulation<br/>
74. Vivo ADAPTS a custom permission (background popover). VA is to return a historical activity to the foreground, which vivo needs.<br/>
73. If there is no bluetooth permission, return the default value (for overseas use)<br/>
72. Fix the uid permission check problem<br/>
71. Security update and file permission control for internal applications<br/>
70. Improve the compatibility of internal app calls, third-party login and sharing<br/>
69. Automatically filter unauthorized external contentproviders<br/>
68. Added function: check the permission of internal app (closed by default)<br/>
67. Model simulation: Build class and build.prop<br/>
66. Improve app compatibility for legu reinforcement<br/>
65. Adapt to Samsung wifimanager<br/>
64. Fix a parameter passing problem in ipc framework (IPCMethod class must be updated)<br/>
63. Complete the hook of 7.0 notice bar<br/>
62. Fix hook of 8.0 dynamic shortcut menu<br/>
61. Setting Handler adds an adaptor interface, mainly suitable for various games<br/>
60. Function changes: Google automatic installation to manual installation, to avoid the first time to start too long<br/>
59. Access to an external Content Provider can be prohibited<br/>
58. Adapt to the number of Huawei desktop icons<br/>
57. Permission classification annotation, indicating that permission can be deleted<br/>
56. Add dual-open mode App to follow the external upgrade switch<br/>
55. Improve jni compatibility of app<br/>
54. Improve compatibility with other plug-in frameworks for app integration<br/>
53. Add setting interface and set according to the package name<br/>
52. Increase the Uri adaptation range to support sharing and viewing files through uris<br/>
51. Fix a problem with Samsung 8.0<br/>
50. Improve the compatibility of app components with the system, better compatible with chrome webview and Google service.<br/>
49. Improve the stability of ART<br/>
48. Increase the scope of camera adaptation<br/>
47. Support shortcut management of internal App under 8.0<br/>
46. Fix exec exceptions<br/>
45. Improve stability (Fix WeChat login backout)<br/>
44. Solve the problem of WeChat database crash<br/>
43. Fixed part 4.4 equipment crash<br/>
42. Fix the problems that the background application is easy to be killed, tudou video black screen, sina weibo cannot be opened, and youku cannot exit after two returns.<br/>
41. Add the survival mechanism of the application, and double-open APP is more difficult to be killed.<br/>
40. Optimize start-up performance of virtual engine.<br/>
39. Compatible with most of the reinforcement and the compatibility of third-party APP increased by 40% compared with the previous version.<br/>
38. Fixed some ROMs with incorrect shortcut icons<br/>
37. Compatibility with previous component Stub File Provider<br/>
36. Virtual IMEI for adapting part of the new ROM<br/>
35. Improve process initialization code and increase stability<br/>
34. Add internal transmission Intent.ACTION_BOOT_COMPLETED broadcast and the switch can be set<br/>
33. Adapt to the Google play game, and support the game to log in with Google<br/>
32. Compatible with Google service framework of android O<br/>
31. Compatible with android O shortcuts<br/>
30. Adapt to headset mode<br/>
29. Some ROMs limit the size of intent. Demo adds zoom shortcut icon code<br/>
28. Fix the Bug in the case of multiple openings<br/>
27. Fix the Bug in Media Controller in some cases<br/>
26. Fix Stub File Provider error of 4.1.2<br/>
25. Share uri processing<br/>
24. Fix the callback of cross-app call Activity<br/>
23. Notification bar blocking switch of foreground service<br/>
22. Attached doc<br/>
21. Improve the CHOOSE callback of intent within VA<br/>
20. The notification bar of Android O is compatible with 2<br/>
19. Optimize ipc framework to improve the survival accuracy of binder<br/>
18. JNI log switch Android.mk: LOCAL_CFLAGS+=-DLOG_ENABLE<br/>
17. Confusion Configuration<br/>
16. Android O Notice Bar Adaptation<br/>
15. Fix problems with some app network CARDS<br/>
14. Dl_open (jni load) compatible with android 8.0<br/>
13. Fix the Bug of Huawei emui8.0<br/>
12. Improve positioning<br/>
11. Set up mobile phone information and imei camouflage algorithm<br/>
10. Adapt to a certain function of 8.0(main app: whats App)<br/>
9. Fix the problem that internal WeChat and other applications could not update pictures and video<br/>
8. Demo adds installation monitoring and automatically updates the application of clone mode<br/>
7. 7.0 File Provider Adaptation<br/>
6. Add location code<br/>
5. Architectural optimization of code<br/>
4. Different features from the open source version<br/>
3. Solve some problems of WeChat sealing<br/>
2. Fix compatibility of some models<br/>
1. Fix 12 minor bugs
