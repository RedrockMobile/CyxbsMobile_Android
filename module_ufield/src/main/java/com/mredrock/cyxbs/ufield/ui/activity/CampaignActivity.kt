package com.mredrock.cyxbs.ufield.ui.activity

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.config.route.UFIELD_CENTER
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.adapter.FragmentVpAdapter
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.ui.fragment.campaignfragment.CheckFragment
import com.mredrock.cyxbs.ufield.ui.fragment.campaignfragment.JoinFragment
import com.mredrock.cyxbs.ufield.ui.fragment.campaignfragment.PublishFragment
import com.mredrock.cyxbs.ufield.ui.fragment.campaignfragment.WatchFragment
import com.mredrock.cyxbs.ufield.viewmodel.MessageViewModel
import kotlin.properties.Delegates
@Route(path = UFIELD_CENTER)
class CampaignActivity : BaseActivity() {


    private val mTabLayout by R.id.ufield_activity_campaign_tl.view<TabLayout>()

    private val mViewPager2 by R.id.ufield_activity_campaign_vp2.view<ViewPager2>()

    private val campaignBack by R.id.ufield_activity_campaign_rl_back.view<RelativeLayout>()

    private var tab1View by Delegates.notNull<View>()

    private var tab2View by Delegates.notNull<View>()

    private var tab3View by Delegates.notNull<View>()

    private var tab4View by Delegates.notNull<View>()

    private val mViewModel by viewModels<MessageViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ufield_activity_campaign)
        initViewClickListener()
        initViewPager2()
        initTabLayout()
        mViewModel.getAllMsg()
    }

    private fun initViewClickListener() {
        campaignBack.setOnSingleClickListener { finish() }
    }

    private fun initViewPager2() {
        mViewPager2.adapter = FragmentVpAdapter(this)
            .add { WatchFragment() }
            .add { JoinFragment() }
            .add { PublishFragment() }
            .add { CheckFragment() }
    }

    private fun initTabLayout() {
        val tabs = arrayOf(
            getString(R.string.ufield_campaign_watch),
            getString(R.string.ufield_campaign_join),
            getString(R.string.ufield_campaign_publish),
            getString(R.string.ufield_campaign_check),
        )
        TabLayoutMediator(
            mTabLayout, mViewPager2
        ) { tab,
            position ->
            tab.text = tabs[position]
        }.attach()
        //设置tabView
        val tab1 = mTabLayout.getTabAt(0)
        val tab2 = mTabLayout.getTabAt(1)
        val tab3 = mTabLayout.getTabAt(2)
        val tab4 = mTabLayout.getTabAt(3)
        tab1View = LayoutInflater.from(this).inflate(R.layout.ufield_activity_campaign_tab1, null)
        tab1?.customView = tab1View
        tab2View = LayoutInflater.from(this).inflate(R.layout.ufield_activity_campaign_tab2, null)
        tab2?.customView = tab2View
        tab3View = LayoutInflater.from(this).inflate(R.layout.ufield_activity_campaign_tab3, null)
        tab3?.customView = tab3View
        tab4View = LayoutInflater.from(this).inflate(R.layout.ufield_activity_campaign_tab4, null)
        tab4?.customView = tab4View
        //设置选中和未选中时的颜色
        val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.customView?.findViewById<TextView>(R.id.ufield_tv_tl_tab)
                    ?.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.ufield_home_tabLayout_text_selected)))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.findViewById<TextView>(R.id.ufield_tv_tl_tab)
                    ?.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.ufield_home_tabLayout_text_unselect)))

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        }
        mTabLayout.addOnTabSelectedListener(onTabSelectedListener)
    }
}