# module_dialog

> 全局弹窗功能抽离

## WebDialog

> 作为android与前端页面通信的桥梁，可以在上线后动态显示前端页面

### Usage

#### 安卓侧

~~~kotlin
// 依赖模块: :module_dialog:api_dialog
ServiceManager.getService<IDialogService>()
    .openDialog(lifecycle, context, url, title) {
        // WebView.() -> Unit
    }
~~~

向前端页面暴露的方法

~~~kotlin
/**
* 使用Rhino脚本引擎运行js代码，用来实现android与js的直接通信
*
* @param code
*/
@JavascriptInterface
fun evalJ2Js(code: String)

/**
* 初始化传感器
* ID 1 加速度传感器
* ID 4 陀螺仪传感器
*/
@JavascriptInterface
fun initSensor(sensorId: Int)

/**
* 拓展接口，Web前端和Android侧可以约定不同的指令来达到不同的功能
*
* @param command
*/
@JavascriptInterface
fun postCustomEvent(command: String)

/**
* 下载文件
* 不推荐前端调用该方法，建议使用url跳转下载，android这边的webView会执行下载回调
*
* @param url
* @param fileName
*/
@JavascriptInterface
fun download(url: String, fileName: String)

/**
* ARouter 路由跳转
*/
fun navigate(route: String)

/**
 * 获得当前登录人的学号
 */
@JavascriptInterface
fun getStu(): String

/**
 * 弹Toast
 *
 * @param str
 */
@JavascriptInterface
fun toast(str: String)

/**
 * 长时间toast
 *
 * @param str
 */
@JavascriptInterface
fun toastLong(str: String)

/**
 * 是否处于黑夜模式
 *
 * @return
 */
@JavascriptInterface
fun isDark(): Boolean

/**
 * 保存图片
 *
 * @param url
 */
@JavascriptInterface
fun savePic(url: String)

/**
 * 通过Android WebView运行一段js代码
 *
 * @param code
 */
@JavascriptInterface
fun executeJs(code: String)

/**
 * 设置onResume生命周期回调
 *
 * @param code
 */
@JavascriptInterface
fun onResume(code: String)

/**
 * 设置onPause生命周期回调
 *
 * @param code
 */
@JavascriptInterface
fun onPause(code: String)

/**
 * 设置onDestroy生命周期回调
 *
 * @param code
 */
@JavascriptInterface
fun onDestroy(code: String)
~~~

需要注意的是，savePic这个动作只会发送EventBus全局事件，具体逻辑需要自行监听事件处理

使用EventBus时需要注意生命周期问题

可以通过继承`EventBusLifecycleSubscriber`把生命周期管理交给基类托管

~~~kotlin
@Subscribe(threadMode = ThreadMode.MAIN)
fun onSavePic(event: DialogSavePicEvent) {
   // ...
}
~~~

#### Web前端侧

与Android通信的映射对象`DialogService`被直接挂载到window对象上

~~~js
const dialogService = globalThis.DialogService

// 弹toast
dialogService.toast("阿巴阿巴")
// 保存图片
dialogService.savePic("https://blog.coldrain.ink/medias/avatar.jpg")
// 跳转到登录界面
dialogService.navigate("/main/login")
~~~

#### 使用rhino引擎实现js与java的直接通信

> rhino引擎已经支持了ES6的很多特性，如解构，let，const，lambda等
>
> rhino 1.7.14已经支持了ES6 Promise，可惜这一版本暂时不支持android

使用例

> 弹一个Toast

~~~js
window.DialogService.evalJ2Js(`com.mredrock.cyxbs.common.component.CyxbsToast.makeText(com.mredrock.cyxbs.common.BaseApp.Companion.getAppContext(), "阿巴阿巴", 0).show()`)
~~~

> 导包 & 匿名内部类

~~~js
importPackage(java.lang)

// 因为之前导了包，所以不用写全称
// 使用一个实现了接口所有方法的object传入构造器来实现一个匿名内部类的效果
const runnable = new Runnable({ run: () => { print("hello world") } })
const thread = new Thread(runnable)
thread.start()
~~~

> 也可以这样导包, 比较类似commonJs的模块化

~~~js
const { Thread, Runnable } = java.lang
~~~

详情参考 [Rhino详解：Java与JS互操作_gladmustang的博客-CSDN博客](https://blog.csdn.net/gladmustang/article/details/41621941)