package com.mredrock.cyxbs.redpage.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.webkit.*
import android.widget.ProgressBar
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.component.JToolbar
import com.mredrock.cyxbs.common.config.REDROCK_HOME_ENTRY
import com.mredrock.cyxbs.common.config.getBaseUrl
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.common.webView.LiteJsWebView
import com.mredrock.cyxbs.redpage.R

/**
 * create by:Fxymine4ever
 * time: 2018/11/7
 */
@Route(path = REDROCK_HOME_ENTRY)
class RedPageWebActivity : BaseActivity() {
    
    private val mToolbar: JToolbar by R.id.tl_redPage.view()
    private val mWebView: LiteJsWebView by R.id.wv_redPage.view()
    private val mProgressBar: ProgressBar by R.id.pb_redPage.view()
    
    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redrock_home_redpage_activity_webview)
        mToolbar.init("红岩网校")
        mToolbar.setBackgroundResource(R.drawable.redrock_home_shape_tl_bg_shape)
        mToolbar.setNavigationOnClickListener { finish() }
        
        val webSettings = mWebView.settings
        webSettings.apply {
            javaScriptEnabled = true//支持js
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK//不使用缓存
            javaScriptCanOpenWindowsAutomatically = true
            allowFileAccess = false
        }
        
        mWebView.apply {
            
            loadUrl("${getBaseUrl()}/app/Public/index/")
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
            mWebView.loadUrl(url)
            return true
        }
        
        override fun onPageFinished(view: WebView?, url: String?) {
            mProgressBar.gone()
        }
        
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            mProgressBar.visible()
        }
    }
    
    private val mWebChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            mProgressBar.progress = newProgress
        }
        
        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            return true
        }
    }
    
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        mWebView.destroy()
    }
}
