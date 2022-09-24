package com.mredrock.cyxbs.course.page.course.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.distinctUntilChanged
import com.mredrock.cyxbs.api.affair.IAffairService
import com.mredrock.cyxbs.api.course.utils.getBeginLesson
import com.mredrock.cyxbs.config.config.SchoolCalendar
import com.mredrock.cyxbs.course.page.course.base.CompareWeekSemesterFragment
import com.mredrock.cyxbs.course.page.course.ui.home.utils.EnterAnimUtils
import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.course.page.course.utils.container.AffairContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.LinkLessonContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.SelfLessonContainerProxy
import com.mredrock.cyxbs.lib.course.helper.affair.CreateAffairDispatcher
import com.mredrock.cyxbs.lib.utils.service.impl
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

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
    // 如果是被异常重启，则不执行动画
    if (!mIsFragmentRebuilt) {
      /**
       * 观察第几周，因为如果是初次进入应用，会因为得不到周数而不主动翻页，所以只能观察该数据
       * 但这是因为主页课表比较特殊而采取观察，其他界面可以直接使用 *VpFragment 的 mNowWeek 变量
       */
      SchoolCalendar.observeWeekOfTerm()
        .firstElement()
        .observeOn(AndroidSchedulers.mainThread())
        .safeSubscribeBy {
          if (it == 0) {
            // 判断周数，只对当前周进行动画
            EnterAnimUtils.startEnterAnim(course, mParentViewModel, viewLifecycleOwner)
          }
        }
    }
  }
  
  private var mIsHappenShowLinkEvent: Boolean? = null
  
  private val mSelfLessonContainerProxy = SelfLessonContainerProxy(this)
  private val mLinkLessonContainerProxy = LinkLessonContainerProxy(this)
  private val mAffairContainerProxy = AffairContainerProxy(this)
  
  private fun initObserve() {
    mParentViewModel.showLinkEvent
      .collectLaunch {
        mIsHappenShowLinkEvent = it
      }
    
    mParentViewModel.homeWeekData
      .distinctUntilChanged()
      .observe { map ->
        val self = map.mapValues { it.value.self }.mapToMinWeek()
        val link = map.mapValues { it.value.link }.mapToMinWeek()
        val affair = map.values.map { it.affair }.flatten()
        mSelfLessonContainerProxy.diffRefresh(self)
        mAffairContainerProxy.diffRefresh(affair)
        mLinkLessonContainerProxy.diffRefresh(link) {
          if (mIsHappenShowLinkEvent == true && mParentViewModel.currentItem == 0 && it.isNotEmpty()) {
            // 这时说明触发了关联人的显示，需要开启入场动画
            mLinkLessonContainerProxy.startAnimation()
          }
        }
      }
  }
  
  private fun initCreateAffair() {
    course.addPointerDispatcher(
      CreateAffairDispatcher(this).apply {
        setOnClickListener {
          IAffairService::class.impl
            .startAffairEditActivity(
              requireContext(),
              0, weekNum - 1, getBeginLesson(startRow), length
            )
        }
      }
    )
  }
}