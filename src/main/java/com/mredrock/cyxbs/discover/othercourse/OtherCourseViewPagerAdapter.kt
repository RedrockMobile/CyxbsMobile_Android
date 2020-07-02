package com.mredrock.cyxbs.discover.othercourse

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mredrock.cyxbs.discover.othercourse.pages.stusearch.OtherCourseSearchFragment

class OtherCourseViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        //若通过fragment的构造器传递参数，会在重建（分屏/旋转）时出现问题
        return OtherCourseSearchFragment().apply { setType(position) }
    }
}