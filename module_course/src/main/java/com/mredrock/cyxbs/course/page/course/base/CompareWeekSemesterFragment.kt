package com.mredrock.cyxbs.course.page.course.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.course.page.course.item.ISingleDayRank
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlapItem
import com.mredrock.cyxbs.lib.course.fragment.page.CourseSemesterFragment

/**
 * 需要比较周数排序的整学期课表页面基类
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
          o1.compareToInternal(o2)
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
        o1.compareToInternal(o2)
      } else 1
    } else {
      if (o2 is ISingleDayRank) -1 else super.compareOverlayItem(row, column, o1, o2)
    }
  }
  
  /**
   * 筛选掉重复的课程，只留下最小周数的课程
   */
  protected fun <T : LessonData> Map<Int, List<T>>.mapToMinWeek(): List<T> {
    val map = hashMapOf<LessonData.Course, T>()
    forEach { entry ->
      entry.value.forEach {
        val value = map[it.course]
        if (value == null || value.week > it.week) {
          map[it.course] = it
        }
      }
    }
    return map.map { it.value }
  }
}