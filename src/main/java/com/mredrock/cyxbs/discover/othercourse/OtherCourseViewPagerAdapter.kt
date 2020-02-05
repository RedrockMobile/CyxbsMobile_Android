package com.mredrock.cyxbs.discover.othercourse

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mredrock.cyxbs.discover.othercourse.pages.stusearch.OtherCourseSearchFragment

class OtherCourseViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return OtherCourseSearchFragment(position)
    }
}