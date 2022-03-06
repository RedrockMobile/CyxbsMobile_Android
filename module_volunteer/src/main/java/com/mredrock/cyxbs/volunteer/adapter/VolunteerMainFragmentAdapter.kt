package com.mredrock.cyxbs.volunteer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by yyfbe, Date on 2020/9/4.
 */
class VolunteerMainFragmentAdapter(fm: FragmentManager, private val fragmentList: List<Fragment>, private val titles: List<String>) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

    override fun getCount(): Int = fragmentList.size

}