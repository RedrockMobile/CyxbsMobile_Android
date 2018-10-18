package com.mredrock.cyxbs.course.adapters

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.mredrock.cyxbs.course.ui.NoCourseInviteFragment

/**
 * Created by anriku on 2018/10/6.
 */

class NoCourseInviteVPAdapter(private val mTiles: List<String>, fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {

    private val mFragmentList = arrayOfNulls<Fragment>(mTiles.size)

    override fun getItem(position: Int): Fragment {
        if (mFragmentList[position] == null){
            mFragmentList[position] = NoCourseInviteFragment().also {
                it.arguments = Bundle().apply {
                    putInt(NoCourseInviteFragment.NOW_WEEK, position)
                }
            }
        }
        return mFragmentList[position]!!
    }

    override fun getCount(): Int {
        return mTiles.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTiles[position]
    }
}
