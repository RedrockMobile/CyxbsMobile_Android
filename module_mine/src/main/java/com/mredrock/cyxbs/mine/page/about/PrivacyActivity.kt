package com.mredrock.cyxbs.mine.page.about

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R


class PrivacyActivity : BaseActivity() {

    private companion object {
        const val USER_PRIVACY_URL = "https://fe-prod.redrock.cqupt.edu.cn/privacy-policy/"
    }

    private val mWebView by R.id.mine_wv_agreement.view<WebView>()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_privacy)
        mWebView.apply {
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


    }
}
