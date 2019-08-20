package com.mredrock.cyxbs.freshman.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mredrock.cyxbs.freshman.view.fragment.CollegeGroupFragment
import com.mredrock.cyxbs.freshman.view.fragment.FellowTownsmanGroupFragment
import com.mredrock.cyxbs.freshman.view.fragment.OnlineActivityFragment

/**
 * Create by yuanbing
 * on 2019/8/3
 */
class OnlineCommunicationPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
    private val mPages = listOf<Fragment>(
            CollegeGroupFragment(),
            FellowTownsmanGroupFragment(),
            OnlineActivityFragment()
    )

    override fun getItem(position: Int) = mPages[position]

    override fun getCount() = mPages.size
}