package com.mredrock.cyxbs.course.page.course.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.course.page.course.item.ISingleDayRank
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlapItem
import com.mredrock.cyxbs.lib.course.fragment.page.CourseSemesterFragment

/**
 * 需要比较周数排序的整学期课表页面
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 14:23
 */
abstract class CompareWeekSemesterFragment : CourseSemesterFragment() {
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initCompare()
    initToday()
  }
  
  protected open fun initCompare() {
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
  protected open fun compareRank(o1: ISingleDayRank, o2: ISingleDayRank): Int {
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