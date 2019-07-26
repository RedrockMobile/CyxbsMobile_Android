package com.mredrock.cyxbs.discover.grades.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
class FPAdapter(fm: androidx.fragment.app.FragmentManager?,
                private val titles: List<String>,
                private val fragments: List<androidx.fragment.app.Fragment>) : androidx.fragment.app.FragmentPagerAdapter(fm) {

    override fun getItem(p0: Int): androidx.fragment.app.Fragment = fragments[p0]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence? = titles[position]
}