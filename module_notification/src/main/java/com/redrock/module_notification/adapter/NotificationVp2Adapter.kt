package com.redrock.module_notification.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:29.
 *
 */
class NotificationVp2Adapter(
    fragmentActivity: FragmentActivity,
    private var fragmentList: List<Fragment>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position] as Fragment
    }

}