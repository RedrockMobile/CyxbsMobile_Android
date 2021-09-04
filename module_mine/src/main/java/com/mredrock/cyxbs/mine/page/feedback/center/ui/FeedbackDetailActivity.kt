package com.mredrock.cyxbs.mine.page.feedback.center.ui

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.databinding.MineActivityFeedbackDetailBinding
class FeedbackDetailActivity : BaseActivity() {
    private lateinit var binding:MineActivityFeedbackDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MineActivityFeedbackDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            tvEditToolbar.text = intent.getStringExtra("title")
            tvDetailContent.text = intent.getStringExtra("content")
        }
    }
}