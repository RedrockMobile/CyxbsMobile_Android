package com.mredrock.cyxbs.freshman.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.*
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.freshman.R
import kotlinx.android.synthetic.main.freshman_activity_about_us.*

class AboutUsActivity : BaseActivity() {
    override val isFragmentActivity = false

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.freshman_activity_about_us)
        common_toolbar.init("红岩网校")

        val setting = webView.settings

        setting.apply {
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK//不使用缓存
            javaScriptCanOpenWindowsAutomatically = true
        }

        webView.apply {
            loadUrl("http://redrock.team/aboutus")
            addJavascriptInterface(this, "android")
            webViewClient = mWebViewClient
        }
    }

    private val mWebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }
}
