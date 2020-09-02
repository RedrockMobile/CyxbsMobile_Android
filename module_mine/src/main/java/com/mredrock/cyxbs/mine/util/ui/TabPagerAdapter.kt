package com.mredrock.cyxbs.mine.util.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TabPagerAdapter(fm: FragmentManager, private val mFragmentsList: List<Fragment>, private val mTitleList: List<String>) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return mFragmentsList[position]
        }

        override fun getCount(): Int {
            return mFragmentsList.size
        }

    override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (mTitleList.size > position) mTitleList[position] else ""
        }
    }