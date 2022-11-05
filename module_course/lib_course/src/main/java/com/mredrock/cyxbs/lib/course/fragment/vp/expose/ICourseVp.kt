package com.mredrock.cyxbs.lib.course.fragment.vp.expose

import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 13:28
 */
interface ICourseVp {
  
  /**
   * 课表页面数量
   */
  val mPageCount: Int
  
  /**
   * 每一页的 [CoursePageFragment]
   */
  fun createFragment(position: Int): CoursePageFragment
  
  /**
   * 课表 vp
   */
  val mViewPager: ViewPager2
  
  /**
   * 课表的 Adapter，只允许使用 [FragmentStateAdapter]
   */
  val mVpAdapter: FragmentStateAdapter
    get() = mViewPager.adapter as FragmentStateAdapter
  
  /**
   * 当前周数
   */
  val mNowWeek: Int
}