package com.mredrock.cyxbs.qa.pages.square.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.square.ui.adapter.NewHotViewPagerAdapter
import com.mredrock.cyxbs.qa.pages.square.ui.fragment.HotFragment
import com.mredrock.cyxbs.qa.pages.square.ui.fragment.LastNewFragment
import com.mredrock.cyxbs.qa.pages.square.viewmodel.CircleDetailViewModel
import kotlinx.android.synthetic.main.qa_activity_circle_detail.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*

class CircleDetailActivity : BaseViewModelActivity<CircleDetailViewModel>() {
    override val isFragmentActivity = true
    private var toolbarTitle="校园周边"

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
        initToolbar()
        initTab()
    }

    private fun initToolbar() {
        qa_ib_toolbar_back.setOnClickListener(View.OnClickListener {
            finish()
            return@OnClickListener
        })
        qa_ib_toolbar_more.gone()
        qa_tv_toolbar_right.setBackgroundResource(R.drawable.qa_ic_dynamic_share)
        qa_tv_toolbar_title.text = toolbarTitle
        qa_ab_layout.setBackgroundResource(R.color.common_window_background)
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