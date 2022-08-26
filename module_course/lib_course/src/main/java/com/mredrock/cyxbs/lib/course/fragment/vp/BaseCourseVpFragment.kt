package com.mredrock.cyxbs.lib.course.fragment.vp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/19 21:11
 */
abstract class BaseCourseVpFragment : BaseFragment() {
  
  protected abstract val pageCount: Int
  protected abstract fun createFragment(position: Int): CoursePageFragment
  protected abstract val mViewPager: ViewPager2
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mViewPager.adapter = CourseAdapter()
  }
  
  protected inner class CourseAdapter : FragmentStateAdapter(this) {
    override fun getItemCount(): Int = pageCount
    override fun createFragment(position: Int): Fragment = this@BaseCourseVpFragment.createFragment(position)
  }
}