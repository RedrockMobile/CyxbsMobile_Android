package com.mredrock.cyxbs.volunteer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class VolunteerFragmentAdapter(fm: FragmentManager, private val fragmentList: MutableList<Fragment>, private val yearList: MutableList<String>) : FragmentStatePagerAdapter(fm) {

    override fun getPageTitle(position: Int): CharSequence? {
        return yearList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }
}