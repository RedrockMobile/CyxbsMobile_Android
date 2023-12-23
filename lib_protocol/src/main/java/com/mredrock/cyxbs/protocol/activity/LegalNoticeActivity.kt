package com.mredrock.cyxbs.protocol.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.webView.LiteJsWebView
import com.mredrock.cyxbs.protocol.R

/**
 * @author: lytMoon
 * @description: 由于掌邮协议变更，现在把负责隐私协议的activity放在lib_protocol中
 * @time: 2023/12/1
 * @version: 1.0
 */

class LegalNoticeActivity : BaseActivity() {

    companion object {
        fun start(context: Context, url: String, title: String) {
            context.startActivity(
                Intent(
                    context,
                    LegalNoticeActivity::class.java
                ).apply {
                    putExtra("url", url)
                    putExtra("title", title)
                })
        }
    }

    private val mWebView by lazy { LiteJsWebView(this) }
    private val mContainerLl by R.id.protocol_webView_container.view<LinearLayout>()
    private val mBackIv by R.id.protocol_mBack.view<ImageView>()
    private val mTitleTv by R.id.protocol_title.view<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.protocol_activity_privacy)
        val url = intent.getStringExtra("url") ?: return
        val title = intent.getStringExtra("title") ?: return
        mTitleTv.text = title
        mWebView.init()
        mWebView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        mWebView.webViewClient = WebViewClient()
        mContainerLl.addView(mWebView)
        mBackIv.setOnClickListener {
            finish()
        }
        onBackPressedDispatcher.addCallback(object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (mWebView.canGoBack()) mWebView.goBack()
                else finish()
            }

        })
        mWebView.loadUrl(url)
    }

    override fun onDestroy() {
        super.onDestroy()
        (mWebView.parent as? ViewGroup)?.removeView(mWebView)
    }
}