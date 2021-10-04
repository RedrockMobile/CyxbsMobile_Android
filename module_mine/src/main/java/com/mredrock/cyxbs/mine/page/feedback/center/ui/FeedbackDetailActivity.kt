package com.mredrock.cyxbs.mine.page.feedback.center.ui

import android.os.Bundle
import android.webkit.WebSettings
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.databinding.MineActivityFeedbackDetailBinding
class FeedbackDetailActivity : BaseActivity() {
    private lateinit var binding: MineActivityFeedbackDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MineActivityFeedbackDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            includeToolBar.tvTitle.text = intent.getStringExtra("title")
            tvDetailContent.apply {
                val setting = settings
                setting.apply {
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
                loadDataWithBaseURL(null, intent.getStringExtra("content"), "text/html", "utf-8", null)
            }
            binding.includeToolBar.btnBack.setOnClickListener { finish() }
        }
    }
}