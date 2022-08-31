package com.mredrock.cyxbs.lib.course.fragment.course.base

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.fragment.course.expose.timeline.ITimeline

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 14:16
 */
@Suppress("LeakingThis")
abstract class TimelineImpl : PeriodImpl(), ITimeline {
  
  final override fun isIncludeTimeline(row: Int): Boolean {
    return row in TIMELINE_LEFT .. TIMELINE_RIGHT
  }
  
  final override fun forEachTimelineRow(block: (row: Int) -> Unit) {
    for (row in TIMELINE_TOP .. TIMELINE_BOTTOM) {
      block.invoke(row)
    }
  }
  
  final override fun forEachTimelineColumn(block: (column: Int) -> Unit) {
    for (column in TIMELINE_LEFT .. TIMELINE_RIGHT) {
      block.invoke(column)
    }
  }
  
  final override fun getTimelineStartWidth(): Int {
    return course.getColumnsWidth(0, TIMELINE_LEFT - 1)
  }
  
  final override fun getTimelineEndWidth(): Int {
    return course.getColumnsWidth(0, TIMELINE_RIGHT)
  }
  
  override val tvLesson1        by R.id.course_tv_lesson_1.view<TextView>()
  override val tvLesson2        by R.id.course_tv_lesson_2.view<TextView>()
  override val tvLesson3        by R.id.course_tv_lesson_3.view<TextView>()
  override val tvLesson4        by R.id.course_tv_lesson_4.view<TextView>()
  override val tvLesson5        by R.id.course_tv_lesson_5.view<TextView>()
  override val tvLesson6        by R.id.course_tv_lesson_6.view<TextView>()
  override val tvLesson7        by R.id.course_tv_lesson_7.view<TextView>()
  override val tvLesson8        by R.id.course_tv_lesson_8.view<TextView>()
  override val tvLesson9        by R.id.course_tv_lesson_9.view<TextView>()
  override val tvLesson10       by R.id.course_tv_lesson_10.view<TextView>()
  override val tvLesson11       by R.id.course_tv_lesson_11.view<TextView>()
  override val tvLesson12       by R.id.course_tv_lesson_12.view<TextView>()
  override val tvNoon           by R.id.course_tv_noon.view<TextView>()
  override val tvDusk           by R.id.course_tv_dusk.view<TextView>()
  
  companion object {
    private const val TIMELINE_LEFT = 0 // 时间轴开始列
    private const val TIMELINE_RIGHT = 0 // 时间轴结束列
    
    private const val TIMELINE_TOP = 0 // 时间轴开始行
    private const val TIMELINE_BOTTOM = 13 // 时间轴开始行
  }
  
  
  
  /////////////////////////////////////
  //
  //             业务逻辑区
  //
  /////////////////////////////////////
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initTimeline()
  }
  
  private fun initTimeline() {
    course.apply {
      // 下面这个 for 用于设置时间轴的初始化宽度
      forEachTimelineColumn {
        setColumnShowWeight(it, 0.8F)
      }
    }
  }
}