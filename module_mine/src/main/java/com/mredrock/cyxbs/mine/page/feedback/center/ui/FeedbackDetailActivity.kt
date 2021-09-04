package com.mredrock.cyxbs.mine.page.feedback.center.ui

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.databinding.MineActivityFeedbackDetailBinding
class FeedbackDetailActivity : BaseActivity() {
    private lateinit var binding: MineActivityFeedbackDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MineActivityFeedbackDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            tvEditToolbar.text = intent.getStringExtra("title")
            tvDetailContent.apply {
                val setting = settings
                setting.apply {
                    javaScriptEnabled = true
                    useWideViewPort = true
                    allowFileAccess = true
                    defaultTextEncodingName = "utf-8";
                }
                loadDataWithBaseURL(null, intent.getStringExtra("content"), "text/html", "utf-8", null)
            }
        }
    }
}