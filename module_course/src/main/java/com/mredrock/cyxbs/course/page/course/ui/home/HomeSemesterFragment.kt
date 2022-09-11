package com.mredrock.cyxbs.course.page.course.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.createViewModelLazy
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.course.page.course.item.ISingleDayRank
import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.course.page.course.utils.container.AffairContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.LinkLessonContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.SelfLessonContainerProxy
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlapItem
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment
import com.mredrock.cyxbs.lib.course.helper.CourseNowTimeHelper
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import java.util.*

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 19:25
 */
class HomeSemesterFragment : CoursePageFragment() {
  
  private val mViewModel by createViewModelLazy(
    HomeCourseViewModel::class,
    { requireParentFragment().viewModelStore }
  )
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initCompare()
    initToday()
    initObserve()
  }
  
  private fun initToday() {
    val calendar = Calendar.getInstance()
    val weekNum = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1
    /*
    * 星期天：1 -> 7
    * 星期一：2 -> 1
    * 星期二：3 -> 2
    * 星期三：4 -> 3
    * 星期四：5 -> 4
    * 星期五：6 -> 5
    * 星期六：7 -> 6
    *
    * 左边一栏是 Calendar.get(Calendar.DAY_OF_WEEK) 得到的数字，
    * 右边一栏是 weekNum 对应的数字
    * */
    showToday(weekNum)
  }
  
  override fun initTimeline() {
    super.initTimeline()
    CourseNowTimeHelper.attach(this)
  }
  
  private val mSelfLessonContainerProxy by lazyUnlock { SelfLessonContainerProxy(this) }
  private val mLinkLessonContainerProxy by lazyUnlock { LinkLessonContainerProxy(this) }
  private val mAffairContainerProxy by lazyUnlock { AffairContainerProxy(this) }
  
  private fun initObserve() {
    mViewModel.homeWeekData
      .observe { map ->
        val values = map.values
        val self = values.map { it.self }.flatten()
        val link = values.map { it.link }.flatten()
        val affair = values.map { it.affair }.flatten()
        mSelfLessonContainerProxy.diffRefresh(self)
        mLinkLessonContainerProxy.diffRefresh(link)
        mAffairContainerProxy.diffRefresh(affair)
      }
  }
  
  private fun initCompare() {
    /**
     * 设置 [course] 内部 View 的添加顺序。因为需要与 [IOverlapItem] 的顺序一致
     */
    course.setCompareLayoutParams { o1, o2 ->
      if (o1 is ISingleDayRank) {
        if (o2 is ISingleDayRank) {
          compareRank(o1, o2)
        } else 1
      } else {
        if (o2 is ISingleDayRank) -1 else o1.compareTo(o2)
      }
    }
  }
  
  /**
   * 设置 [IOverlapItem] 的顺序，专门用于实现了 [ISingleDayRank] 接口的比较
   */
  override fun compareOverlayItem(row: Int, column: Int, o1: IOverlapItem, o2: IOverlapItem): Int {
    return if (o1 is ISingleDayRank) {
      if (o2 is ISingleDayRank) {
        compareRank(o1, o2)
      } else 1
    } else {
      if (o2 is ISingleDayRank) -1 else super.compareOverlayItem(row, column, o1, o2)
    }
  }
  
  /**
   * 比较 [ISingleDayRank]，但判断了是否实现 [IWeek]，
   * 在整学期的页面中，周数也需要进行比较
   */
  private fun compareRank(o1: ISingleDayRank, o2: ISingleDayRank): Int {
    return if (o1 is IWeek) {
      if (o2 is IWeek) {
        ISingleDayRank.compareBy(o2.week - o1.week) { // 周数小的显示在上面
          o1.compareToInternal(o2)
        }
      } else 1
    } else {
      if (o2 is IWeek) -1 else o1.compareToInternal(o2)
    }
  }
}