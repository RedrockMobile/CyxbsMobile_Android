package com.mredrock.cyxbs.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mredrock.cyxbs.common.config.DISCOVER_ENTRY
import com.mredrock.cyxbs.common.config.MINE_ENTRY
import com.mredrock.cyxbs.common.config.QA_ENTRY
import com.mredrock.cyxbs.common.service.ServiceManager

/**
 * Created by dingdeqiao on 2021/3/16
 * 主页功能页Adapter
 */
class MainAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ServiceManager.getService(DISCOVER_ENTRY) as Fragment
            1 -> ServiceManager.getService(QA_ENTRY) as Fragment
            2 -> ServiceManager.getService(MINE_ENTRY) as Fragment
            else -> ServiceManager.getService(DISCOVER_ENTRY) as Fragment
        }
    }
}