package com.mredrock.cyxbs.declare.pages.main.page.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.declare.databinding.DeclareActivityHomeBinding
import com.mredrock.cyxbs.declare.pages.detail.page.activity.DetailActivity
import com.mredrock.cyxbs.declare.pages.main.page.adapter.HomeRvAdapter
import com.mredrock.cyxbs.declare.pages.main.page.viewmodel.HomeViewModel
import com.mredrock.cyxbs.lib.base.ui.BaseBindActivity
import com.mredrock.cyxbs.lib.utils.extensions.setOnDoubleClickListener

class HomeActivity : BaseBindActivity<DeclareActivityHomeBinding>() {
    companion object {
        /**
         * 启动表态主页
         */
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }
    private val mViewModel by viewModels<HomeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val declareHomeRvAdapter = HomeRvAdapter()
        binding.run {
            declareHomeRecyclerview.run {
                layoutManager = LinearLayoutManager(this@HomeActivity)
                adapter = declareHomeRvAdapter
                declareHomeToolbarTv.setOnDoubleClickListener {
                    smoothScrollToPosition(0)
                }
            }
        }
        declareHomeRvAdapter.setOnItemClickedListener {
            DetailActivity.startActivity(this, it)
        }
        binding.declareHomeToolbarPost.setOnClickListener {
            //跳至自己发布过的话题页面
            PostedActivity.startActivity(this)
        }
        binding.declareIvToolbarArrowLeft.setOnClickListener {
            finish()
        }
        mViewModel.homeLiveData.observe {
            if (it.isEmpty()) {
                binding.declareHomeNoData.visibility = View.VISIBLE
            } else {
                binding.declareHomeNoData.visibility = View.GONE
                declareHomeRvAdapter.submitList(it)
            }
        }
        mViewModel.homeErrorLiveData.observe {
            if (it) {
                binding.declareHomeCl.visibility = View.GONE
                binding.declareHomeNoNet.visibility = View.VISIBLE
            } else {
                binding.declareHomeCl.visibility = View.VISIBLE
                binding.declareHomeNoNet.visibility = View.GONE
            }
        }
        mViewModel.permLiveData.observe {
            binding.declareHomeToolbarPost.visibility = if (it.isPerm) View.VISIBLE else View.GONE
        }
        mViewModel.hasPerm()
        mViewModel.getHomeData()
    }
}