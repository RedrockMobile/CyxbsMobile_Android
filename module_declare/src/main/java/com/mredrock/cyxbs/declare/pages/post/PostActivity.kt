package com.mredrock.cyxbs.declare.pages.post

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.declare.databinding.DeclareActivityPostBinding
import com.mredrock.cyxbs.declare.pages.post.adapter.PostSectionRvAdapter
import com.mredrock.cyxbs.lib.base.ui.BaseBindActivity

class PostActivity : BaseBindActivity<DeclareActivityPostBinding>() {

    override val isCancelStatusBar: Boolean
        get() = false

    private val viewModel by viewModels<PostViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null
        binding.rvTopic.apply {
            layoutManager = LinearLayoutManager(this@PostActivity)
            adapter = PostSectionRvAdapter()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}