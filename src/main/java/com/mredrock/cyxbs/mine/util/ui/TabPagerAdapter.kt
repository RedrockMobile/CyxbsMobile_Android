package com.mredrock.cyxbs.mine.util.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import android.view.ViewGroup

class TabPagerAdapter(fm: androidx.fragment.app.FragmentManager, private val mFragmentsList: List<androidx.fragment.app.Fragment>?, private val mTitleList: List<String>) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): androidx.fragment.app.Fragment? {
            return if (mFragmentsList == null || mFragmentsList.isEmpty()) null else mFragmentsList[position]
        }

        override fun getCount(): Int {
            return mFragmentsList?.size ?: 0
        }

    override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (mTitleList.size > position) mTitleList[position] else ""
        }
    }