package com.mredrock.cyxbs.login.page.privacy

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.login.R

class PrivacyActivity : BaseActivity() {
    
    companion object {
        const val USER_AGREEMENT_URL = "https://fe-prod.redrock.cqupt.edu.cn/privacy-policy"
    }
    
    private val mWebView by R.id.login_wv_privacy.view<WebView>()
    private val mBack by R.id.login_view_privacy_back.view<View>()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_privacy)
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
            loadUrl(USER_AGREEMENT_URL)
            
            // 适配黑夜模式
            val uiMode = resources.configuration.uiMode
            if ((uiMode and  Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES){
                loadUrl("javascript:showInfoFromJava('true')")
            }
        }
    
        mBack.setOnSingleClickListener {
            finish()
        }
    }
}