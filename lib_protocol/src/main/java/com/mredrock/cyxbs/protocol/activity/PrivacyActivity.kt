package com.mredrock.cyxbs.protocol.activity

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.ImageView
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.webView.LiteJsWebView
import com.mredrock.cyxbs.config.route.PRIVACY_PROTOCOL
import com.mredrock.cyxbs.protocol.R

/**
 * @author: lytMoon
 * @description: 由于掌邮协议变更，现在把负责隐私协议的activity放在lib_protocol中，并解决了WebView错误用法的隐私泄露（删除 login 和 mine 模块对应部分）
 * @time: 2023/12/1
 * @version: 1.0
 */

@Route(path = PRIVACY_PROTOCOL)
class PrivacyActivity : BaseActivity() {

    private companion object {
        const val USER_PRIVACY_URL = "https://fe-prod.redrock.cqupt.edu.cn/privacy-policy/"
    }

    private val mWebView by lazy { LiteJsWebView(applicationContext) }
    private val webViewParentContainer by R.id.protocol_webView_container.view<LinearLayout>()
    private val mBack by R.id.protocol_mBack.view<ImageView>()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.protocol_activity_privacy)
        mWebView.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.apply {
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
            loadUrl(USER_PRIVACY_URL)
            // 适配黑夜模式
            val uiMode = resources.configuration.uiMode
            if ((uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                loadUrl("javascript:showInfoFromJava('true')")
            }
        }
        webViewParentContainer.addView(mWebView)

        mBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeWebView()
    }

    private fun removeWebView() {
        (mWebView.parent as? ViewGroup)?.removeView(mWebView)
    }
}