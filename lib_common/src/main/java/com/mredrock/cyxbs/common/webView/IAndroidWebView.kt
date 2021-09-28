package com.mredrock.cyxbs.common.webView

import android.os.Handler
import android.os.Message
import android.webkit.JavascriptInterface
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager

/**
 * 新增的AndroidWebView直接继承这个类
 * 具体写参考AndroidWebView
 * 高阶函数不用管，直接继承重写就可以
 * 如果出现白屏问题，原因可能是Js代码中调用了这边没有实现的代码
 */
abstract class IAndroidWebView(
    private val handler: Handler?,
    private val lExeByAndroid: ((String) -> Unit)? = {},
    private val lToastByAndroid: ((String) -> Unit)? = {},
    private val lShowInfoFromJs: ((String) -> Unit)? = {},
    private val lSavePicByAndroid: ((String) -> Unit)? = {
        val message = Message()
        message.what = 0
        message.obj = it
        handler?.sendMessage(message)
    },
    private val lJumpARouterByAndroid: ((String) -> Unit)? = {
        ARouter.getInstance().build(it).navigation()
    }
) : WebViewBaseCallBack {

    var music: String? = null
    var sensorIDs: ArrayList<Int> = ArrayList()

    //调用这个方法来保存图片
    @JavascriptInterface
    open fun savePicByAndroid(str: String) {
        lSavePicByAndroid?.invoke(str)
    }

    /**
     * 初始化背景音乐
     * @musicJs 播放音乐的Js命令
     */
    @JavascriptInterface
    open fun initMusicByAndroid(musicJs: String) {
        music = musicJs
    }

    /**
     * 初始化传感器
     * ID 1 加速度传感器
     * ID 4 陀螺仪传感器
     */
    @JavascriptInterface
    open fun initSensorByAndroid(sensorId: Int) {
        sensorIDs.add(sensorId)
    }

    /**
     * 通过@Arouter进行端内跳转
     * 传入 RoutingTable.kt 表中的路徑
     */
    @JavascriptInterface
    open fun jumpARouterByAndroid(str: String) {
        lJumpARouterByAndroid?.invoke(str)
    }

    /**
     * 传入Js到Android来执行
     */
    @JavascriptInterface
    open fun exeByAndroid(str: String) {
        lExeByAndroid?.invoke(str)
    }

    /**
     * 弹toast
     */
    @JavascriptInterface
    open fun toastByAndroid(str: String) {
        lToastByAndroid?.invoke(str)
    }

    /**
     * 获得当前登录人的学号
     */
    @JavascriptInterface
    open fun getStuByAndroid(): String {
        return ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()
    }

    @JavascriptInterface
    open fun isDarkByAndroid(): Boolean {
        return BaseApp.context.applicationContext.resources.configuration.uiMode == 0x21
    }
}