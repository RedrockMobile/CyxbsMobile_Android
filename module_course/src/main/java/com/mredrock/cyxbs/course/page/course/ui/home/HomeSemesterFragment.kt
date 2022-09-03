package com.mredrock.cyxbs.course.page.course.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.createViewModelLazy
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.course.page.course.item.IRank
import com.mredrock.cyxbs.course.page.course.item.affair.Affair
import com.mredrock.cyxbs.course.page.course.item.lesson.LinkLesson
import com.mredrock.cyxbs.course.page.course.item.lesson.SelfLesson
import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.lib.course.fragment.item.IOverlapItem
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment

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
    initObserve()
  }
  
  private fun initObserve() {
    mViewModel.homeWeekData
      .observe { map ->
        clearLesson()
        clearAffair()
        map.values.forEach { result ->
          addLesson(result.selfLesson.map { SelfLesson(it) })
          addLesson(result.linkLesson.map { LinkLesson(it) })
          addAffair(result.affair.map { Affair(it) })
        }
      }
  }
  
  private fun initCompare() {
    course.setCompareLayoutParams { o1, o2 ->
      if (o1 is IRank) {
        if (o2 is IRank) {
          compareRank(o1, o2)
        } else 1
      } else {
        if (o2 is IRank) -1 else o1.compareTo(o2)
      }
    }
  }
  
  override fun compareOverlayItem(row: Int, column: Int, o1: IOverlapItem, o2: IOverlapItem): Int {
    return if (o1 is IRank) {
      if (o2 is IRank) {
        compareRank(o1, o2)
      } else 1
    } else {
      if (o2 is IRank) -1 else super.compareOverlayItem(row, column, o1, o2)
    }
  }
  
  private fun compareRank(o1: IRank, o2: IRank): Int {
    return if (o1 is IWeek) {
      if (o2 is IWeek) {
        IRank.compareBy(o2.week - o1.week) { // 周数小的显示在上面
          o1.compareToInternal(o2)
        }
      } else 1
    } else {
      if (o2 is IWeek) -1 else o1.compareToInternal(o2)
    }
  }
}