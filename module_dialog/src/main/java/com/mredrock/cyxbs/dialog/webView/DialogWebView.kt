package com.mredrock.cyxbs.dialog.webView

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.lifecycle.Lifecycle

/**
 * com.mredrock.cyxbs.dialog.webView.DialogWebView
 * CyxbsMobile_Android
 *
 * @author 寒雨
 * @since 2022/8/2 15:11
 */
class DialogWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : WebView(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * 初始化webView，使用前请调用该方法
     *
     * @param lifecycle 所在界面Lifecycle
     * @param jsInterface (名称 to 实例) 挂载到前端window的对象实例,
     * 实例类中请使用@JavascriptInterface向前端暴露可访问方法
     */
    fun initialize(lifecycle: Lifecycle, jsInterface: Pair<String, DialogJsInterface>) {
        settings.apply {
            //支持js
            javaScriptEnabled = true
            // 允许js弹窗
            javaScriptCanOpenWindowsAutomatically = true
//            //支持 DOM 缓存
//            domStorageEnabled = true
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
        val (name, obj) = jsInterface
        obj.attach(name, this)
        lifecycle.addObserver(obj)
    }
}