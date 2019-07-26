package com.mredrock.cyxbs.main.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Created By jay68 on 2018/8/21.
 */
class MainVpAdapter(fm: androidx.fragment.app.FragmentManager, private val fragments: List<androidx.fragment.app.Fragment>) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size
}