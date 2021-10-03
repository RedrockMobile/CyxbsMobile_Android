package com.mredrock.cyxbs.main.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.main.R
import kotlinx.android.synthetic.main.main_activity_privacy.*

class PrivacyActivity : BaseActivity() {
    companion object{
        const val USER_AGREEMENT_URL = "https://fe-prod.redrock.cqupt.edu.cn/privacy-policy"
    }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_privacy)
        main_wv_privacy.settings.apply {
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
        main_wv_privacy.loadUrl(USER_AGREEMENT_URL)
        
        main_user_privacy_back.setOnClickListener {
            finish()
        }
    }
}