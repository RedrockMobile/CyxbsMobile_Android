package com.mredrock.cyxbs.discover.pages.discover.webView

import android.os.Handler
import com.mredrock.cyxbs.common.webView.AndroidWebView
import com.mredrock.cyxbs.common.webView.IAndroidWebView

class WebViewFactory(
    private val url:String = "",
    private val handler: Handler,
    private val exe:(String) -> Unit,
    private val toast:(String) -> Unit
    ) {


    fun produce(): IAndroidWebView {
        //这里的设想是根据网址来返回对应的类
        return when(url){
            else -> AndroidWebView(handler,exe,toast)
        }
    }
}