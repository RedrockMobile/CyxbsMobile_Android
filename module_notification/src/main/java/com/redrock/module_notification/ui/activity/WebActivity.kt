package com.redrock.module_notification.ui.activity

import android.content.Context
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.startActivity
import com.redrock.module_notification.R
import kotlinx.android.synthetic.main.activity_web.*

/**
 * Author by OkAndGreat
 * Date on 2022/5/3 19:24.
 *
 */

//禁止跳转
class ForbidJumpWebViewClient : WebViewClient() {
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        view?.loadUrl(request?.url.toString())
        return super.shouldInterceptRequest(view, request)
    }
}

class WebActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        val url = intent.getStringExtra("URL")

        notification_wv.webViewClient = ForbidJumpWebViewClient()

        notification_detail_back.setOnClickListener { finish() }
        url?.let { notification_wv.loadUrl(it) }

        //必须调用
        notification_wv.init()

    }

    override fun onDestroy() {
        super.onDestroy()
        notification_wv.destroy()
    }

    companion object {
        fun startWebViewActivity(url: String, context: Context) {
            context.startActivity<WebActivity>(
                "URL" to url
            )
        }
    }
}