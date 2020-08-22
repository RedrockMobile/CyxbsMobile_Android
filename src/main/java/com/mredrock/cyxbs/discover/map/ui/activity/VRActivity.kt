package com.mredrock.cyxbs.discover.map.ui.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mredrock.cyxbs.common.ui.BaseActivity

class VRActivity : BaseActivity() {
    override val isFragmentActivity = false
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSlideable = false
    }

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    override fun onStart() {
        super.onStart()

        webView = WebView(this)
        setContentView(webView)

        val dialog = ProgressDialog(this)
        dialog.setMessage("加载中...")
        dialog.show()

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.settings.mediaPlaybackRequiresUserGesture = false
        }
        webView.loadUrl("http://720yun.com/t/0e929mp6utn?pano_id=473004")
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    dialog.dismiss()
                }
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, url)
            }
        }
    }

    override fun onStop() {
        webView.destroy()
        super.onStop()
    }
}
