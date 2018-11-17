package com.mredrock.cyxbs.volunteer.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import 	android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import com.mredrock.cyxbs.common.utils.LogUtils


class VolunteerFragmentAdapter(fm: FragmentManager, fragmentList: MutableList<Fragment>, yearList: MutableList<String>) : FragmentStatePagerAdapter(fm) {
    private val fragmentList: MutableList<Fragment> = fragmentList
    private val yearList: MutableList<String> = yearList

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