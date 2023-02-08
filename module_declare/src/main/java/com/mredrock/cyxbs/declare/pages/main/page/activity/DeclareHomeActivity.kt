package com.mredrock.cyxbs.declare.pages.main.page.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.declare.databinding.DeclareActivityHomeBinding
import com.mredrock.cyxbs.declare.pages.main.HomeDataBean
import com.mredrock.cyxbs.declare.pages.main.page.adapter.DeclareHomeRvAdapter
import com.mredrock.cyxbs.declare.pages.main.page.viewmodel.DeclareHomeViewModel
import com.mredrock.cyxbs.declare.pages.main.utils.FiveAndDoubleClickListener
import com.mredrock.cyxbs.declare.pages.main.utils.setOnFiveAndDoubleClickListener
import com.mredrock.cyxbs.lib.base.ui.BaseBindActivity
import com.mredrock.cyxbs.lib.utils.extensions.setOnDoubleClickListener
import com.mredrock.cyxbs.lib.utils.service.ServiceManager

class DeclareHomeActivity : BaseBindActivity<DeclareActivityHomeBinding>() {
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
                    override fun doubleClick(view: View) {
                        smoothScrollToPosition(0)
                    }

                    override fun fiveClick(view: View) {
                        binding.declareHomeToolbarPost.visibility = View.VISIBLE
                    }
                })
            }
        }
        mViewModel.homeLiveData.observe {
            declareHomeRvAdapter.submitList(it)
        }
        binding.declareHomeToolbarPost.setOnClickListener {

        }
        mViewModel.getHomeData()
    }
}