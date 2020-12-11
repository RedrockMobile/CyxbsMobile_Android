package com.mredrock.cyxbs.qa.pages.square.ui.activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.square.ui.adapter.NewHotViewPagerAdapter
import com.mredrock.cyxbs.qa.pages.square.ui.fragment.HotFragment
import com.mredrock.cyxbs.qa.pages.square.ui.fragment.LastNewFragment
import com.mredrock.cyxbs.qa.pages.square.viewmodel.CircleDetailViewModel
import kotlinx.android.synthetic.main.qa_activity_circle_detail.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import kotlinx.android.synthetic.main.qa_recycler_item_circle_square.*
import kotlinx.android.synthetic.main.qa_recycler_item_circle_square.view.*

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
        initTab()
        initView()
        initClick()
    }

    private fun initClick() {
        qa_circle_detail_iv_back.setOnSingleClickListener {
            finish()
        }
    }

    private fun initView() {
        tv_circle_square_name.text="校园周边"
        tv_circle_square_descriprion.text="重邮也有猫猫图鉴啦，欢迎大家一起来分享你看见的猫猫~"
        tv_circle_square_person_number.text=477.toString()+"个成员"
         btn_circle_square_concern.text="+关注"
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