package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mredrock.cyxbs.qa.pages.search.ui.fragment.RelateDynamicFragment

/**
 * @class
 * @author YYQF
 * @data 2021/9/24
 * @description
 **/
class DynamicPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragmentList = ArrayList<Fragment>()
    fun addFragment(fragment: Fragment){
        fragmentList.add(fragment)
    }
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}