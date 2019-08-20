package com.mredrock.cyxbs.freshman.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.view.fragment.RouteFragment
import com.mredrock.cyxbs.freshman.view.fragment.SceneryFragment

/**
 * Create by roger
 * on 2019/8/8
 */
class CampusMapPagerAdapter (manager: FragmentManager) : FragmentPagerAdapter(manager) {
    private val mPages = listOf(
            RouteFragment(),
            SceneryFragment()
    )

    private val pagesTitle = listOf(BaseApp.context.resources.getString(R.string.freshman_tab_layout_campus_map_bus),
            BaseApp.context.resources.getString(R.string.freshman_tab_layout_campus_map_scenery))

    override fun getItem(position: Int) = mPages[position] as Fragment

    override fun getCount() = mPages.size

    override fun getPageTitle(position: Int): CharSequence? {
        return pagesTitle[position]
    }
}