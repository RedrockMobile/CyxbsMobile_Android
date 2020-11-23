package com.mredrock.cyxbs.qa.pages.square.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.square.ui.adapter.NewHotViewPagerAdapter
import com.mredrock.cyxbs.qa.pages.square.ui.fragment.HotFragment
import com.mredrock.cyxbs.qa.pages.square.ui.fragment.LastNewFragment
import com.mredrock.cyxbs.qa.pages.square.viewmodel.CircleDetailViewModel
import kotlinx.android.synthetic.main.qa_activity_circle_detail.*

class CircleDetailActivity : BaseViewModelActivity<CircleDetailViewModel>() {
    override val isFragmentActivity = true
    private val lastNewFragment by lazy(LazyThreadSafetyMode.NONE) {
        LastNewFragment()
    }
    private val hotFragment by lazy(LazyThreadSafetyMode.NONE) {
        HotFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_circle_detail)
        qa_vp_circle_detail.adapter=NewHotViewPagerAdapter(this, listOf(lastNewFragment,hotFragment))

    }

    private fun initToolbar() {

    }

    private fun initTab() {
        TabLayoutMediator(qa_circle_detail_tab, qa_vp_circle_detail) { tab, position ->
            when (position) {
                0 -> tab.text = "最新"
                1 -> tab.text = "热门"
            }
        }.attach()
    }

}