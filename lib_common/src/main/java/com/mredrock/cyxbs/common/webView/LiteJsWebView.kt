package com.mredrock.cyxbs.common.webView

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast

/**
 * 传入的实现类应该继承 IAndroidWebView,如果仅使用这个自定义类的话，生命周期回调是没有效果的
 * 这个仅支持简单的使用(参考IAndroidWebView),arouter跳转,toast,得到学号,黑夜模式,执行Js代码
 * 其余参考RollerViewActivity实现
 * 一定要调用init方法
 */
class LiteJsWebView : WebView {
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):super(context,attrs,defStyleAttr)
    constructor(context: Context,attrs: AttributeSet?):super(context,attrs)
    constructor(context: Context):super(context)

    @SuppressLint("SetJavaScriptEnabled")
    fun init(
        androidWebView: IAndroidWebView = AndroidWebView(
            exe = {
                //执行Js代码
                this.post {
                    this.evaluateJavascript(it) { }
                }
            },
            toast = {
                //弹toast
                CyxbsToast.makeText(BaseApp.appContext, it, Toast.LENGTH_SHORT).show()
            }
        )
    ) {
        //基本配置
        this.settings.apply {
            //支持js
            javaScriptEnabled = true
            //支持 DOM 缓存
            domStorageEnabled = true
            //将图片调整到适合webView的大小
            useWideViewPort = true
            //缩放至屏幕的大小
            loadWithOverviewMode = true
            //支持缩放
            setSupportZoom(true)
            //设置内置的缩放控件。若为false，则该WebView不可缩放
            builtInZoomControls = true
            //隐藏原生的缩放控件
            displayZoomControls = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            //这里必须为false，因为他为true则必须等到用户交互之后才行
            mediaPlaybackRequiresUserGesture = false
        }
        //加载js文件的
        this.addJavascriptInterface(androidWebView, androidWebView::class.simpleName.toString())
    }

}