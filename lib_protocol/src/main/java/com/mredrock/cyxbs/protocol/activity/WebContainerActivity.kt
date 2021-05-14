package com.mredrock.cyxbs.protocol.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.protocol.R
import com.mredrock.cyxbs.protocol.config.PROTOCOL_WEB_CONTAINER
import kotlinx.android.synthetic.main.protocol_activity_web_container.*


@Route(path = PROTOCOL_WEB_CONTAINER)
class WebContainerActivity : BaseActivity() {

    companion object {
        const val URI = "uri"

        fun loadWebPage(context: Context, uri: String) {
            context.startActivity(Intent(context, WebContainerActivity::class.java).apply {
                putExtra(URI, uri)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = intent.getStringExtra(URI)
        setContentView(R.layout.protocol_activity_web_container)


        val dialog = ProgressDialog(this)
        dialog.setMessage("加载中...")
        dialog.show()

        web_view.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            mediaPlaybackRequiresUserGesture = false
        }
        web_view.loadUrl(uri)
        web_view.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress >= 100) {
                    dialog.dismiss()
                }
            }
        }
        web_view.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, url)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        web_view.resumeTimers()
    }

    override fun onPause() {
        super.onPause()
        web_view.pauseTimers()
    }


    override fun onDestroy() {
        super.onDestroy()
        web_view.destroy()
    }

}