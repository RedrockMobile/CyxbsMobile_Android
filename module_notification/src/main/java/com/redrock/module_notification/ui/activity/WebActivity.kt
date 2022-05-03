package com.redrock.module_notification.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.startActivity
import com.redrock.module_notification.R
import kotlinx.android.synthetic.main.activity_web.*

/**
 * Author by OkAndGreat
 * Date on 2022/5/3 19:24.
 *
 */
class WebActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        val url = intent.getStringExtra("URL")

        notification_detail_back.setOnClickListener { finish() }

        notification_wv.loadUrl("baidu.com")


    }

    override fun onDestroy() {
        super.onDestroy()
        notification_wv.destroy()
    }

    companion object {
        fun startWebViewActivity(url: String, context: Context) {
            context.startActivity<WebActivity>(
                "URL" to url
            )
        }
    }
}