package com.mredrock.cyxbs.mine.page.about

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.webView.LiteJsWebView
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.USER_AGREEMENT_URL

class AgreementActivity : BaseActivity() {
    private val mine_wv_agreement by R.id.mine_wv_agreement.view<LiteJsWebView>()
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_agreement)
        mine_wv_agreement.settings.apply {
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
        mine_wv_agreement.loadUrl(USER_AGREEMENT_URL)
    }
}