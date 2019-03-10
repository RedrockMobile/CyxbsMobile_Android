package com.mredrock.cyxbs.mine.util.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup

class TabPagerAdapter(fm: FragmentManager, private val mFragmentsList: List<Fragment>?, private val mTitleList: List<String>) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
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