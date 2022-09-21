package com.mredrock.cyxbs.course.page.course.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.createViewModelLazy
import com.mredrock.cyxbs.course.page.course.base.CompareWeekSemesterFragment
import com.mredrock.cyxbs.course.page.course.ui.home.utils.EnterAnimUtils
import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.course.page.course.utils.container.AffairContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.LinkLessonContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.SelfLessonContainerProxy
import com.mredrock.cyxbs.lib.course.helper.affair.CreateAffairDispatcher

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
    initEntrance()
    initObserve()
    initCreateAffair()
  }
  
  private fun initEntrance() {
    if (!mIsFragmentRebuilt) {
      // 如果是被异常重启，则不执行动画
      if (mParentViewModel.nowWeek == 0) {
        // 判断周数，只对当前周进行动画
        EnterAnimUtils.startEnterAnim(course, mParentViewModel, viewLifecycleOwner)
      }
    }
  }
  
  private var mIsNeedStartLinkLessonEntranceAnim: Boolean? = null
  
  private val mSelfLessonContainerProxy = SelfLessonContainerProxy(this)
  private val mLinkLessonContainerProxy = LinkLessonContainerProxy(this)
  private val mAffairContainerProxy = AffairContainerProxy(this)
  
  private fun initObserve() {
    mParentViewModel.showLinkEvent.collectLaunch {
      mIsNeedStartLinkLessonEntranceAnim = it
    }
    
    mParentViewModel.homeWeekData
      .observe { map ->
        val self = map.mapValues { it.value.self }.mapToMinWeek()
        val link = map.mapValues { it.value.link }.mapToMinWeek()
        val affair = map.values.map { it.affair }.flatten()
        mSelfLessonContainerProxy.diffRefresh(self)
        mAffairContainerProxy.diffRefresh(affair)
        mLinkLessonContainerProxy.diffRefresh(link) {
          if (mIsNeedStartLinkLessonEntranceAnim == true) {
            // 这时说明触发了关联人的显示，需要实现入场动画
            // 使用 mIsNeedStartLinkLessonEntranceAnim 很巧妙的避开了 Fragment 重建数据倒灌的问题
            mLinkLessonContainerProxy.startAnimation()
          }
        }
      }
  }
  
  private fun initCreateAffair() {
    course.addPointerDispatcher(CreateAffairDispatcher(this))
  }
}