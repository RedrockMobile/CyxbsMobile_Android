package com.mredrock.cyxbs.common.webView

import android.os.Handler
import com.mredrock.cyxbs.common.webView.IAndroidWebView


/**
 * 如果是要暴露给js调用的接口 请加上 [@JavascriptInterface] 注释
 */
class AndroidWebView(
    handler: Handler? = null,
    exe: (String) -> Unit = {},
    toast: (String) -> Unit = {}
) : IAndroidWebView(handler, exe, toast) {


    override fun webViewResume() {

    }

    override fun webViewPause() {

    }

    override fun webViewDestroy() {

    }


}