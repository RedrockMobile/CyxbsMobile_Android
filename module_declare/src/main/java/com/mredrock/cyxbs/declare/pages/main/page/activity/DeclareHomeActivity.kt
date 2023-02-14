package com.mredrock.cyxbs.declare.pages.main.page.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.declare.databinding.DeclareActivityHomeBinding
import com.mredrock.cyxbs.declare.pages.detail.page.activity.DeclareDetailActivity
import com.mredrock.cyxbs.declare.pages.main.page.adapter.DeclareHomeRvAdapter
import com.mredrock.cyxbs.declare.pages.main.page.viewmodel.DeclareHomeViewModel
import com.mredrock.cyxbs.declare.pages.main.utils.FiveAndDoubleClickListener
import com.mredrock.cyxbs.declare.pages.main.utils.setOnFiveAndDoubleClickListener
import com.mredrock.cyxbs.lib.base.ui.BaseBindActivity

class DeclareHomeActivity : BaseBindActivity<DeclareActivityHomeBinding>() {
    companion object {
        /**
         * 启动表态主页
         */
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, DeclareHomeActivity::class.java))
        }
    }

    private val mViewModel by viewModels<DeclareHomeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val declareHomeRvAdapter = DeclareHomeRvAdapter()
        binding.run {
            declareHomeRecyclerview.run {
                layoutManager = LinearLayoutManager(this@DeclareHomeActivity)
                adapter = declareHomeRvAdapter
                declareHomeBar.setOnFiveAndDoubleClickListener(listener = object :
                    FiveAndDoubleClickListener {
                    override fun doubleClick(view: View) {//双击
                        smoothScrollToPosition(0)
                    }

                    override fun fiveClick(view: View) {//五击
                        binding.declareHomeToolbarPost.visibility = View.VISIBLE
                    }
                })
            }
        }
        declareHomeRvAdapter.setOnItemClickedListener {
            DeclareDetailActivity.startActivity(this, it)
        }
        binding.declareHomeToolbarPost.setOnClickListener {
            //跳至发布话题页面
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
        mViewModel.errorLiveData.observe {
            if (it) {
                binding.declareHomeCl.visibility = View.GONE
                binding.declareHomeNoNet.visibility = View.VISIBLE
            } else {
                binding.declareHomeCl.visibility = View.VISIBLE
                binding.declareHomeNoNet.visibility = View.GONE
            }
        }
        mViewModel.getHomeData()
    }
}