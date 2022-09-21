package com.mredrock.cyxbs.course.page.course.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.createViewModelLazy
import com.mredrock.cyxbs.course.page.course.base.CompareWeekSemesterFragment
import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.course.page.course.utils.container.AffairContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.LinkLessonContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.SelfLessonContainerProxy
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 19:25
 */
class HomeSemesterFragment : CompareWeekSemesterFragment() {
  
  private val mParentViewModel by createViewModelLazy(
    HomeCourseViewModel::class,
    { requireParentFragment().viewModelStore }
  )
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initObserve()
  }
  
  private val mSelfLessonContainerProxy by lazyUnlock { SelfLessonContainerProxy(this) }
  private val mLinkLessonContainerProxy by lazyUnlock { LinkLessonContainerProxy(this) }
  private val mAffairContainerProxy by lazyUnlock { AffairContainerProxy(this) }
  
  private fun initObserve() {
    mParentViewModel.homeWeekData
      .observe { map ->
        val values = map.values
        val self = values.map { it.self }.flatten()
        val link = values.map { it.link }.flatten()
        val affair = values.map { it.affair }.flatten()
        mSelfLessonContainerProxy.diffRefresh(self) { tryStartEntranceAnim() }
        mLinkLessonContainerProxy.diffRefresh(link) { tryStartEntranceAnim() }
        mAffairContainerProxy.diffRefresh(affair) { tryStartEntranceAnim() }
      }
  }
  
  // 是否允许入场动画
  private var mIsAllowEntranceAnim = true
  
  /**
   * 尝试执行入场动画
   */
  private fun tryStartEntranceAnim() {
    if (mIsFragmentRebuilt) {
      // 如果 Fragment 处于被摧毁重建的状态，那么取消入场动画
      return
    }
    if (mIsAllowEntranceAnim) {
      mIsAllowEntranceAnim = false
      if (mParentViewModel.nowWeek == 0) {
        startEntranceAnim()
      }
    }
  }
}