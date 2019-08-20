package com.mredrock.cyxbs.freshman.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.bean.DormitoryAndCanteenText
import com.mredrock.cyxbs.freshman.view.adapter.CampusGuidelinesPagerAdapter
import com.mredrock.cyxbs.freshman.view.widget.CustomSecondTabLayout

class CampusGuidelinesFragment(val data: DormitoryAndCanteenText) : BaseFragment() {
    private lateinit var mTab: CustomSecondTabLayout
    private lateinit var mViewPager: ViewPager
    private lateinit var mAdapter: CampusGuidelinesPagerAdapter
    private lateinit var mTabView: FrameLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.freshman_fragment_campus_guidelines, container, false)
        mTabView = view.findViewById(R.id.include_custom_second_tab_layout)
        mTab = CustomSecondTabLayout(view.findViewById(R.id.rv_custom_second_tab_layout))
        mTabView.gone()
        mViewPager = view.findViewById(R.id.vp_campus_guidelines_second)
        initTabLayout()
        mTabView.visible()
        initViewPager()
        return view
    }

    private fun initTabLayout() {
        if (data.message.size > 4) mTab.mTabMode = TabLayout.MODE_SCROLLABLE
        for (message in data.message) {
            mTab.addTab(message.name)
        }
        mTab.commit()
        mTab.addOnTabSelectedListener { mViewPager.currentItem = it }
    }

    private fun initViewPager() {
        mAdapter = CampusGuidelinesPagerAdapter(childFragmentManager)
        mViewPager.adapter = mAdapter
        mAdapter.refreshData(List(data.message.size) { DormitoryAndCanteenFragment(data.message[it]) })
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                mTab.select(position)
            }
        })
    }
}