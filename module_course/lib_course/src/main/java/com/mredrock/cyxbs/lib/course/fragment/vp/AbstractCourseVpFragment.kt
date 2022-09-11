package com.mredrock.cyxbs.lib.course.fragment.vp

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.course.fragment.vp.expose.ICourseVp
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/19 21:11
 */
abstract class AbstractCourseVpFragment : BaseFragment(), ICourseVp {
  
  override val mVpAdapter: FragmentStateAdapter by lazyUnlock { CourseAdapter(this) }
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mViewPager.adapter = mVpAdapter
//    mViewPager.offscreenPageLimit = 1 // 预加载一页，减少页面刚加载时就滑动的卡顿
  }
  
  private inner class CourseAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = mPageCount
    override fun createFragment(position: Int): Fragment = this@AbstractCourseVpFragment.createFragment(position)
  }
}