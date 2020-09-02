package com.mredrock.cyxbs.discover.pages

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.startActivity
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.network.RollerViewInfo
import kotlinx.android.synthetic.main.discover_activity_roller_view.*

class RollerViewActivity : BaseActivity() {
    override val isFragmentActivity = false

    @SuppressLint("ObsoleteSdkInt", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discover_activity_roller_view)

        common_toolbar.init(intent.getStringExtra("Key"))

        val dialog = ProgressDialog(this)
        dialog.setMessage("加载中...")
        dialog.show()

        val url = intent.getStringExtra("URL")
        discover_web_view.settings.apply {
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
        discover_web_view.loadUrl(url)
        discover_web_view.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress >= 100) {
                    dialog.dismiss()
                }
            }
        }
        discover_web_view.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, url)
            }
        }
    }

    companion object {

        fun startRollerViewActivity(info: RollerViewInfo, context: Context) {
            context.startActivity<RollerViewActivity>("URL" to info.picture_goto_url, "Key" to info.keyword)
        }
    }
}
