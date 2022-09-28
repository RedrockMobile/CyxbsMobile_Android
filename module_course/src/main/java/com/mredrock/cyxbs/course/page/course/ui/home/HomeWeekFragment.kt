package com.mredrock.cyxbs.course.page.course.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.mredrock.cyxbs.api.affair.IAffairService
import com.mredrock.cyxbs.api.course.utils.getBeginLesson
import com.mredrock.cyxbs.config.config.SchoolCalendar
import com.mredrock.cyxbs.course.page.course.ui.home.utils.EnterAnimUtils
import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.course.page.course.utils.container.AffairContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.LinkLessonContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.SelfLessonContainerProxy
import com.mredrock.cyxbs.lib.course.fragment.page.CourseWeekFragment
import com.mredrock.cyxbs.lib.course.helper.affair.CreateAffairDispatcher
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer
import com.mredrock.cyxbs.lib.utils.service.impl
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 19:25
 */
class HomeWeekFragment : CourseWeekFragment() {
  
  companion object {
    fun newInstance(week: Int): HomeWeekFragment {
      return HomeWeekFragment().apply {
        arguments = bundleOf(
          "mWeek" to week
        )
      }
    }
  }
  
  override val mWeek by arguments<Int>()
  
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
          if (it == mWeek) {
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
      .map { it[mWeek] ?: HomeCourseViewModel.HomePageResult }
      .distinctUntilChanged()
      .observe {
        mSelfLessonContainerProxy.diffRefresh(it.self)
        mAffairContainerProxy.diffRefresh(it.affair)
        mLinkLessonContainerProxy.diffRefresh(it.link) { data ->
          if (mIsHappenShowLinkEvent == true && mParentViewModel.currentItem == mWeek && data.isNotEmpty()) {
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
            .startActivityForAddAffair(mWeek, lp.weekNum - 1, getBeginLesson(lp.startRow), lp.length)
          remove()
        }
      }
    )
  }
  
  override fun initFoldLogic() {
    super.initFoldLogic()
    if (!mIsFragmentRebuilt) {
      // 只有在第一次显示 Fragment 时才进行监听，后面的折叠状态会由 CourseFoldHelper 保存
      course.addItemExistListener(
        object : IItemContainer.OnItemExistListener {
          override fun onItemAddedAfter(item: IItem, view: View) {
            val lp = item.lp
            if (compareNoonPeriod(lp.startRow) * compareNoonPeriod(lp.endRow) <= 0) {
              unfoldNoon()
            }
            if (compareDuskPeriod(lp.startRow) * compareDuskPeriod(lp.endRow) <= 0) {
              unfoldDusk()
            }
          }
        }
      )
    }
  }
}