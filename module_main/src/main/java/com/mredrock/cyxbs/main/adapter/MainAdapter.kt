package com.mredrock.cyxbs.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mredrock.cyxbs.config.route.*
import com.mredrock.cyxbs.lib.utils.service.ServiceManager

/**
 * Created by dingdeqiao on 2021/3/16
 * 主页功能页Adapter
 */
class MainAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
  override fun getItemCount() = 3
  override fun createFragment(position: Int): Fragment {
    return when (position) {
      0 -> ServiceManager.fragment(DISCOVER_ENTRY)
//      1 -> ServiceManager.fragment(QA_ENTRY)
      /**
       * TODO 关闭服务 邮问
       */
      1 -> ServiceManager.fragment(FAIRGROUND_ENTRY)
      2 -> ServiceManager.fragment(MINE_ENTRY)
      else -> error("??? 改了 getItemCount() 为什么不改这个 ?")
    }
  }
}