package com.mredrock.cyxbs.grades.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
class FPAdapter(fm: FragmentManager?,
                private val titles: List<String>,
                private val fragments: List<Fragment>) : FragmentPagerAdapter(fm) {

    override fun getItem(p0: Int): Fragment = fragments[p0]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence? = titles[position]
}