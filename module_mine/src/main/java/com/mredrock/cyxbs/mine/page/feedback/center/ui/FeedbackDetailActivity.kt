package com.mredrock.cyxbs.mine.page.feedback.center.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityFeedbackDetailBinding

class FeedbackDetailActivity : AppCompatActivity() {

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