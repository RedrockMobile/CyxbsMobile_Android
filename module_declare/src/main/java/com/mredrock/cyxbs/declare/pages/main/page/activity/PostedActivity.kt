package com.mredrock.cyxbs.declare.pages.main.page.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.declare.R
import com.mredrock.cyxbs.declare.databinding.DeclareActivityHomeBinding
import com.mredrock.cyxbs.declare.pages.detail.page.activity.DetailActivity
import com.mredrock.cyxbs.declare.pages.main.page.adapter.HomeRvAdapter
import com.mredrock.cyxbs.declare.pages.main.page.viewmodel.HomeViewModel
import com.mredrock.cyxbs.declare.pages.post.PostActivity
import com.mredrock.cyxbs.lib.base.ui.BaseBindActivity
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.setOnDoubleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.visible

/**
 * 因为发布过投票的页面和主页面差不多，所以这里就共用了主页面的xml
 */
class PostedActivity : BaseBindActivity<DeclareActivityHomeBinding>() {
    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, PostedActivity::class.java))
        }
    }

    private val mViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val declareHomeRvAdapter = HomeRvAdapter()
        binding.declareHomeToolbarPost.visibility = View.VISIBLE
        binding.run {
            declareHomeRecyclerview.run {
                layoutManager = LinearLayoutManager(this@PostedActivity)
                adapter = declareHomeRvAdapter
                declareHomeToolbarTv.text = resources.getString(R.string.declare_posted_title)
                declareHomeToolbarTv.setOnDoubleClickListener {
                    smoothScrollToPosition(0)
                }
            }
            declareHomeNoDataPic.setImageResource(R.drawable.declare_ic_posted_no_data)
            declareHomeNoDataTv.text = resources.getString(R.string.declare_posted_no_data)
        }
        declareHomeRvAdapter.setOnItemClickedListener {
            DetailActivity.startActivity(this, it)
        }
        binding.declareHomeToolbarPost.setOnClickListener {
            //跳至自己发布话题页面
            PostActivity.start(this)
        }
        binding.declareIvToolbarArrowLeft.setOnClickListener {
            finish()
        }
        mViewModel.postedLiveData.observe {
            if (it.isEmpty()) {
                binding.declareHomeNoData.visible()
            } else {
                binding.declareHomeNoData.gone()
                declareHomeRvAdapter.submitList(it)
            }
        }
        mViewModel.postedErrorLiveData.observe {
            if (it) {
                binding.declareHomeCl.gone()
                binding.declareHomeNoNet.visible()
            } else {
                binding.declareHomeCl.visible()
                binding.declareHomeNoNet.gone()
            }
        }
        mViewModel.getPostedVotes()
    }
}