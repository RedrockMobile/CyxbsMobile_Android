package com.mredrock.cyxbs.ufield.lyt.ui

import android.os.Bundle
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.adapter.FragmentVpAdapter
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lyt.fragment.searchfragment.AllSearchFragment
import com.mredrock.cyxbs.ufield.lyt.fragment.searchfragment.CultureSearchFragment
import com.mredrock.cyxbs.ufield.lyt.fragment.searchfragment.EducationSearchFragment
import com.mredrock.cyxbs.ufield.lyt.fragment.searchfragment.SportsSearchFragment


/**
 * description ：负责搜索功能的activity
 * author : lytMoon
 * email : yytds@foxmail.com
 * date : 2023/8/8 16:34
 * version: 1.0
 */
class SearchActivity : BaseActivity() {

    private val mTabLayout: TabLayout by R.id.uField_search_tabLayout.view()
    private val mVp: ViewPager2 by R.id.uField_search_viewpager2.view()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ufield_activity_search)
        iniTab()
    }

    /**
     * 初始化tabLayout
     */
    private fun iniTab() {
        mVp.adapter = FragmentVpAdapter(this)
            .add { AllSearchFragment() }
            .add { CultureSearchFragment() }
            .add { SportsSearchFragment() }
            .add { EducationSearchFragment() }
        TabLayoutMediator(mTabLayout, mVp) { tab, position ->
            when (position) {
                0 -> tab.text = "全部活动"
                1 -> tab.text = "文娱活动"
                2 -> tab.text = "体育活动"
                else -> tab.text = "教育活动"
            }
        }.attach()
        //让初始化的第一个先变色
        (mTabLayout.getChildAt(0) as ViewGroup).getChildAt(0)
            .setBackgroundResource(R.drawable.ufield_ic_tab_shape_selected)
        for (i in 0 until mTabLayout.tabCount) {
            val tab = (mTabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val params = tab.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(10, params.topMargin, 10, params.bottomMargin)
            tab.requestLayout()
        }
        /**
         * 实现滑动到特定区域和没有的颜色区分
         */
        mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.view.setBackgroundResource(R.drawable.ufield_ic_tab_shape_selected)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.view.setBackgroundResource(R.drawable.ufield_ic_tab_shape)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


    }

}