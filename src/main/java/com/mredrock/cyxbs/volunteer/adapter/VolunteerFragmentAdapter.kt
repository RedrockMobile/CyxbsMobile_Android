package com.mredrock.cyxbs.volunteer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import    androidx.fragment.app.FragmentStatePagerAdapter


class VolunteerFragmentAdapter(fm: androidx.fragment.app.FragmentManager, fragmentList: MutableList<androidx.fragment.app.Fragment>, yearList: MutableList<String>) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {
    private val fragmentList: MutableList<androidx.fragment.app.Fragment> = fragmentList
    private val yearList: MutableList<String> = yearList

    override fun getPageTitle(position: Int): CharSequence? {
        return yearList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return fragmentList[position]
    }
}