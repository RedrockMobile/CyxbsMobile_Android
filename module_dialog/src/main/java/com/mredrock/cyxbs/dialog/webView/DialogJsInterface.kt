@file:Suppress("unused")

package com.mredrock.cyxbs.dialog.webView

import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.util.Log
import android.webkit.*
import androidx.annotation.Keep
import androidx.lifecycle.*
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.api.dialog.DialogWebEvent
import com.mredrock.cyxbs.api.dialog.IDialogService
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.dialog.DialogService
import com.mredrock.cyxbs.dialog.activity.PhotoActivity
import com.mredrock.cyxbs.lib.base.BaseApp.Companion.baseApp
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.extensions.toastLong
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import org.mozilla.javascript.Context

/**
 * com.mredrock.cyxbs.dialog.webView.DialogJsInterface
 * CyxbsMobile_Android
 *
 * 优化廖老师的Web容器，使用Lifecycle和EventBus解耦部分逻辑
 * 引入Rhino脚本引擎，实现java层与js层的互调用
 *
 * @author 寒雨
 * @since 2022/8/2 15:27
 */
@Keep
class DialogJsInterface : DefaultLifecycleObserver {

    private lateinit var webView: WebView
    private var onPauseLogic: String? = null
    private var onResumeLogic: String? = null
    private var onDestroyLogic: String? = null
    private var onPageFinished: String? = null
    private var manager: SensorManager? = null
    private val sensorIDs = mutableListOf<Int>()
    private val sensorEventListeners = mutableListOf<SensorEventListener>()

    /**
     * 使用Rhino脚本引擎运行js代码，用来实现android与js的直接通信
     *
     * @param code
     */
    @JavascriptInterface
    fun evalJ2Js(code: String) {
        val context = Context.enter()
        context.optimizationLevel = -1
        val scope = context.initStandardObjects()
        // 初始化标准对象
        try {
            Log.d("DialogJsInterface", "evalJ2Js: ${
                context.evaluateString(scope, code, null, 1, null)
            }")
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            Context.exit()
        }
    }

    /**
     * 初始化传感器
     * ID 1 加速度传感器
     * ID 4 陀螺仪传感器
     */
    @JavascriptInterface
    fun initSensor(sensorId: Int) {
        sensorIDs.add(sensorId)
    }

    /**
     * 拓展接口，Web前端和Android侧可以约定不同的指令来达到不同的功能
     *
     * @param command
     */
    @JavascriptInterface
    fun postCustomEvent(command: String) {
        (ServiceManager.invoke(IDialogService::class) as DialogService).mutableEventChannel.tryEmit(
            DialogWebEvent.Custom(command)
        )
    }

    /**
     * 下载文件
     * 不推荐前端调用该方法，建议使用url跳转下载，android这边的webView会执行下载回调
     *
     * @param url
     * @param fileName
     */
    @JavascriptInterface
    fun download(url: String, fileName: String) {
        (ServiceManager.invoke(IDialogService::class) as DialogService).mutableEventChannel.tryEmit(
            DialogWebEvent.Download(url, fileName)
        )
    }

    /**
     * ARouter 路由跳转
     */
    @JavascriptInterface
    fun navigate(route: String) {
        ARouter.getInstance().build(route).navigation()
    }

    /**
     * 获得当前登录人的学号
     */
    @JavascriptInterface
    fun getStu(): String {
        return ServiceManager.invoke(IAccountService::class).getUserService().getStuNum()
    }

    @JavascriptInterface
    fun toast(str: String) {
        str.toast()
    }

    @JavascriptInterface
    fun toastLong(str: String) {
        str.toastLong()
    }

    @JavascriptInterface
    fun isDark(): Boolean {
        return baseApp.applicationContext.resources.configuration.uiMode == 0x21
    }

    @JavascriptInterface
    fun savePic(url: String) {
        (ServiceManager.invoke(IDialogService::class) as DialogService).mutableEventChannel.tryEmit(
            DialogWebEvent.SavePic(url)
        )
    }

    @JavascriptInterface
    fun eval(code: String) {
        webView.evaluateJavascript(code) { }
    }

    @JavascriptInterface
    fun onResume(code: String) {
        onResumeLogic = code
    }

    @JavascriptInterface
    fun onPause(code: String) {
        onPauseLogic = code
    }

    @JavascriptInterface
    fun onDestroy(code: String) {
        onDestroyLogic = code
    }

    @JavascriptInterface
    fun onPageFinished(code: String) {
        onPageFinished = code
    }

    override fun onPause(owner: LifecycleOwner) {
        onPauseLogic?.let { webView.evaluateJavascript(it, null) }
        // 暂停整个webView
        webView.pauseTimers()
        // 注销传感器
        sensorEventListeners.forEach {
            manager?.unregisterListener(it)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        onResumeLogic?.let { webView.evaluateJavascript(it, null) }
        webView.resumeTimers()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        onDestroyLogic?.let { webView.evaluateJavascript(it, null) }
        webView.destroy()
    }

    /**
     * 该方法会覆写webViewClient & downloadListener & onLongClickListener
     * 如需做一些自定义的设置，请在attach之后酌情覆写
     *
     * @param name
     * @param webView
     */
    fun attach(name: String, webView: WebView) {
        this.webView = webView
        WebView.setWebContentsDebuggingEnabled(true)
        webView.apply {
            // 页面渲染完成回调
            webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return super.shouldOverrideUrlLoading(view, url)
                }

                override fun onPageFinished(view: WebView, url: String?) {
                    super.onPageFinished(view, url)
                    initSenors()
                    view.post {
                        onPageFinished?.let { evaluateJavascript(it) {} }
                    }
                }
            }
            webChromeClient = WebChromeClient()
            // 下载逻辑处理
            setDownloadListener { url, _, contentDisposition, mimetype, _ ->
                val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
                download(url, fileName)
            }
            // 长按逻辑处理
            setOnLongClickListener { view ->
                val result = (view as WebView).hitTestResult
                val type = result.type

                if (type == WebView.HitTestResult.UNKNOWN_TYPE) return@setOnLongClickListener false
                when (type) {
                    WebView.HitTestResult.IMAGE_TYPE -> {
                        // 图片逻辑处理
                        val imgUrl = result.extra ?: return@setOnLongClickListener true
                        PhotoActivity.activityStart(context, arrayListOf(imgUrl), 0, null)
                    }
                    //如果是长按超链接就跳转
                    WebView.HitTestResult.SRC_ANCHOR_TYPE -> {
                        // 这样跳转应该会直接跳转到浏览器
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.extra))
                        context.startActivity(intent)
                    }
                }
                true
            }
            // 触摸反馈
            // 结果点什么都要弹个toast，真的烦人，先注释掉
            // 弹不弹toast还是交给前端页面比较好
//            onTouch { view, motionEvent ->
//                when(motionEvent.action){
//                    MotionEvent.ACTION_DOWN ->{
//                        val result = (view as WebView).hitTestResult
//                        when(result.type){
//                            WebView.HitTestResult.SRC_ANCHOR_TYPE -> {
//                                toast("长按即可跳转到浏览器打开")
//                            }
//                        }
//                    }
//                }
//            }
            // 挂载JsInterface到window实例
            addJavascriptInterface(this@DialogJsInterface, name)
        }
    }

    private fun initSenors() {
        if (sensorIDs.isEmpty()) return
        manager = webView.context.getSystemService(SENSOR_SERVICE) as SensorManager
        sensorIDs.forEach {
            when (it) {
                // 螺旋传感器
                Sensor.TYPE_GYROSCOPE -> {
                    val gyroscope = manager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
                    gyroscope?.apply {
                        val listener = object : SensorEventListener {
                            override fun onSensorChanged(event: SensorEvent) {
                                // 执行前端定义的逻辑, 若没有则不执行
                                eval("""
                                    console.log('Test')
                                    if (window.gyroscope) {
                                        console.log('Test2')
                                        window.gyroscope('${event.values[0]}', '${event.values[1]}', '${event.values[2]}')
                                    }
                                """.trimIndent())
                            }

                            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }
                        }
                        val registerFunc: () -> Unit = {
                            manager!!.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
                        }
                        registerFunc()
                        sensorEventListeners.add(listener)
                    }
                }
                // 加速度传感器
                Sensor.TYPE_ACCELEROMETER -> {
                    val accelerometer = manager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                    accelerometer?.apply {
                        val listener = object : SensorEventListener {
                            override fun onSensorChanged(event: SensorEvent) {
                                // 执行前端定义的逻辑, 若没有则不执行
                                eval("""
                                    console.log('Test')
                                    if (window.accelerometer) {
                                        console.log('Test2')
                                        window.accelerometer('${event.values[0]}', '${event.values[1]}', '${event.values[2]}')
                                    }
                                """.trimIndent())
                            }

                            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }
                        }
                        val registerFunc: () -> Unit = {
                            manager!!.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
                        }
                        registerFunc()
                        sensorEventListeners.add(listener)
                    }
                }
            }
        }
    }
}