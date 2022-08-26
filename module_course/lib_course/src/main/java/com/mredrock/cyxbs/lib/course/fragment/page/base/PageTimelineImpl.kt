package com.mredrock.cyxbs.lib.course.fragment.page.base

import com.mredrock.cyxbs.lib.course.fragment.page.timeline.ITimeLine

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 14:16
 */
abstract class PageTimelineImpl : PagePeriodImpl(), ITimeLine {
  
  final override fun isIncludeTimeLine(row: Int): Boolean {
    return row in TIME_LINE_LEFT .. TIME_LINE_RIGHT
  }
  
  final override fun forEachTimeLine(block: (column: Int) -> Unit) {
    for (column in TIME_LINE_LEFT .. TIME_LINE_RIGHT) {
      block.invoke(column)
    }
  }
  
  final override fun getTimeLineStartWidth(): Int {
    return course.getColumnsWidth(0, TIME_LINE_LEFT - 1)
  }
  
  final override fun getTimeLineEndWidth(): Int {
    return course.getColumnsWidth(0, TIME_LINE_RIGHT)
  }
  
  companion object {
    private const val TIME_LINE_LEFT = 0 // 时间轴开始列
    private const val TIME_LINE_RIGHT = 0 // 时间轴结束列
  }
  
  
  
  /////////////////////////////////////
  //
  //             业务逻辑区
  //
  /////////////////////////////////////
  
  override fun initCourseInternal() {
    super.initCourseInternal()
    initTimeLine()
  }
  
  private fun initTimeLine() {
    course {
      // 下面这个 for 用于设置时间轴的初始化宽度
      forEachTimeLine {
        setColumnShowWeight(it, 0.8F)
        mNlWeek.setColumnShowWeight(it, 0.8F)
      }
    }
  }
}