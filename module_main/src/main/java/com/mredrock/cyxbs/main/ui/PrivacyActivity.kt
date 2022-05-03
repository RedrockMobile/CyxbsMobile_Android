package com.mredrock.cyxbs.main.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.main.R
import kotlinx.android.synthetic.main.main_activity_privacy.*

class PrivacyActivity : BaseActivity() {
    companion object {
        const val USER_AGREEMENT_URL = "https://fe-prod.redrock.cqupt.edu.cn/privacy-policy"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_privacy)
        main_wv_privacy.loadUrl(USER_AGREEMENT_URL)

        main_user_privacy_back.setOnClickListener { finish() }

        /**
         * 传值给前端让前端知晓用户是否处于黑夜模式从而页面黑夜适配
         */
        fun sendInfoToJs() {
            val darkModeInfo = applicationContext.resources.configuration.uiMode == 0x21
            main_wv_privacy.loadUrl("javascript:showInfoFromJava('$darkModeInfo')")
        }
    }
}