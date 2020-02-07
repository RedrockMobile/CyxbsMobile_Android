package com.mredrock.cyxbs.course.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mredrock.cyxbs.common.config.WEEK_NUM
import com.mredrock.cyxbs.course.ui.NoCourseInviteFragment

/**
 * Created by anriku on 2018/10/6.
 */

class NoCourseInviteVPAdapter(private val mTiles: List<String>, fm: FragmentManager) :
        androidx.fragment.app.FragmentStatePagerAdapter(fm) {

    private val mFragmentList = arrayOfNulls<Fragment>(mTiles.size)

    override fun getItem(position: Int): Fragment {
        if (mFragmentList[position] == null){
            mFragmentList[position] = NoCourseInviteFragment().also {
                it.arguments = Bundle().apply {
                    putInt(WEEK_NUM, position + 1)
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
