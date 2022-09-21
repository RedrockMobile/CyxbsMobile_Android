package com.mredrock.cyxbs.lib.course.fragment.course.expose.timeline

import android.widget.TextView
import com.mredrock.cyxbs.lib.course.fragment.course.expose.period.am.IAmPeriod
import com.mredrock.cyxbs.lib.course.fragment.course.expose.period.dusk.IDuskPeriod
import com.mredrock.cyxbs.lib.course.fragment.course.expose.period.night.INightPeriod
import com.mredrock.cyxbs.lib.course.fragment.course.expose.period.noon.INoonPeriod
import com.mredrock.cyxbs.lib.course.fragment.course.expose.period.pm.IPmPeriod

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 15:46
 */
interface ITimeline : IAmPeriod, INoonPeriod, IPmPeriod, IDuskPeriod, INightPeriod {
  
  fun isIncludeTimeline(row: Int): Boolean
  
  fun forEachTimelineRow(block: (row: Int) -> Unit)
  
  fun forEachTimelineColumn(block: (column: Int) -> Unit)
  
  fun getTimelineStartWidth(): Int
  
  fun getTimelineEndWidth(): Int
  
  val tvLesson1: TextView
  val tvLesson2: TextView
  val tvLesson3: TextView
  val tvLesson4: TextView
  val tvLesson5: TextView
  val tvLesson6: TextView
  val tvLesson7: TextView
  val tvLesson8: TextView
  val tvLesson9: TextView
  val tvLesson10: TextView
  val tvLesson11: TextView
  val tvLesson12: TextView
  val tvNoon: TextView
  val tvDusk: TextView
}