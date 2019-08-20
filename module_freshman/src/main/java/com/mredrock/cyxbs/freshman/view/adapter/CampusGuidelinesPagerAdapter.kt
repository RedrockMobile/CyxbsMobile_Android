package com.mredrock.cyxbs.freshman.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class CampusGuidelinesPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
    private var mFragments: List<Fragment> = listOf()

    override fun getItem(position: Int) = mFragments[position]

    override fun getCount() = mFragments.size

    fun refreshData(fragments: List<Fragment>) {
        mFragments = fragments
        notifyDataSetChanged()
    }
}