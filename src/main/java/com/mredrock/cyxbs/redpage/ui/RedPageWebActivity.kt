package com.mredrock.cyxbs.redpage.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.webkit.*
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.END_POINT_REDROCK
//import com.jude.swipbackhelper.SwipeBackHelper
import com.mredrock.cyxbs.common.config.REDROCK_HOME_ENTRY
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.redpage.R
import kotlinx.android.synthetic.main.redpage_activity_webview.*

/**
 * create by:Fxymine4ever
 * time: 2018/11/7
 */
@Route(path = REDROCK_HOME_ENTRY)
class RedPageWebActivity : BaseActivity() {
    override val isFragmentActivity: Boolean = false

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redpage_activity_webview)
        tl_redPage.init("红岩网校")
        tl_redPage.background = resources.getDrawable(R.drawable.redrock_home_tl_bg_shape)
        tl_redPage.setNavigationOnClickListener { finish() }
//        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false)

        val webSettings = wv_redPage.settings
        webSettings.apply {
            javaScriptEnabled = true//支持js
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK//不使用缓存
            javaScriptCanOpenWindowsAutomatically = true
        }

        wv_redPage.apply {

            loadUrl("$END_POINT_REDROCK/app/Public/index/")
            addJavascriptInterface(this, "android")
            webChromeClient = mWebChromeClient
            webViewClient = mWebViewClient
        }


    }

    private val mWebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            try {
                if (url.startsWith("baidu")) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                    return true
                }

            } catch (e: Exception) {
                return false
            }
            wv_redPage.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            pb_redPage.gone()
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            pb_redPage.visible()
        }
    }

    private val mWebChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            pb_redPage.progress = newProgress
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            return true
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KEYCODE_BACK && wv_redPage.canGoBack()) {
            wv_redPage.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        wv_redPage.destroy()
    }
}
