package com.mredrock.cyxbs.declare.pages.main.page.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.declare.R
import com.mredrock.cyxbs.declare.databinding.DeclareActivityHomeBinding
import com.mredrock.cyxbs.declare.databinding.DeclareActivityPostBinding
import com.mredrock.cyxbs.declare.pages.main.HomeDataBean
import com.mredrock.cyxbs.declare.pages.main.page.adapter.DeclareHomeRvAdapter
import com.mredrock.cyxbs.declare.pages.main.page.viewmodel.DeclareHomeViewModel
import com.mredrock.cyxbs.lib.base.ui.BaseBindActivity
import com.mredrock.cyxbs.lib.utils.extensions.setOnDoubleClickListener

class DeclareHomeActivity : BaseBindActivity<DeclareActivityHomeBinding>() {
    private val mViewModel by viewModels<DeclareHomeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val declareHomeRvAdapter = DeclareHomeRvAdapter()
        binding.run {
            declareHomeRecyclerview.run {
                layoutManager = LinearLayoutManager(this@DeclareHomeActivity)
                adapter = declareHomeRvAdapter
                declareHomeBar.setOnDoubleClickListener {
                    smoothScrollToPosition(0)
                }
            }
        }
        mViewModel.homeLiveData.observe {
            declareHomeRvAdapter.submitList(it)
        }

        mViewModel.getHomeData()
    }
}