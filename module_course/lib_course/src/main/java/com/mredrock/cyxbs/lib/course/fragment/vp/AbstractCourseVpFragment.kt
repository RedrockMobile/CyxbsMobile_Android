package com.mredrock.cyxbs.lib.course.fragment.vp

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.course.fragment.vp.expose.ICourseVp

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/19 21:11
 */
abstract class AbstractCourseVpFragment : BaseFragment(), ICourseVp {
  
  override val mVpAdapter: FragmentStateAdapter = CourseAdapter()
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mViewPager.adapter = mVpAdapter
  }
  
  private inner class CourseAdapter : FragmentStateAdapter(this) {
    override fun getItemCount(): Int = mPageCount
    override fun createFragment(position: Int): Fragment = this@AbstractCourseVpFragment.createFragment(position)
  }
}