package com.mredrock.cyxbs.course.adapters

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.course.ui.CourseFragment

/**
 * Created by anriku on 2018/8/16.
 */

class ScheduleVPAdapter(private val mTiles: Array<String>, fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {

    private val mFragmentList = arrayOfNulls<Fragment>(mTiles.size)

    override fun getItem(position: Int): Fragment {
        if (mFragmentList[position] == null) {
            mFragmentList[position] = CourseFragment().also {
                it.arguments = Bundle().apply {
                    putInt(CourseFragment.WEEK_NUM, position)
                }
            }
        }
        return mFragmentList[position]!!
    }

    override fun getCount() = mTiles.size


    override fun getPageTitle(position: Int): CharSequence? {
        return mTiles[position]
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}