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
  
  /**
   * 跟时间轴所在列作对比
   * @return 1、返回正数，说明 [column] 在时间轴所在列右边；2、返回负数，说明在左边；3、返回 0，说明在里面
   */
  fun compareTimelineColumn(column: Int): Int
  
  /**
   * 以行遍历时间轴
   */
  fun forEachTimelineRow(block: (row: Int) -> Unit)
  
  /**
   * 以列遍历时间轴
   */
  fun forEachTimelineColumn(block: (column: Int) -> Unit)
  
  /**
   * 得到时间轴开始的宽度值（距离课表左边缘）
   */
  fun getTimelineStartWidth(): Int
  
  /**
   * 得到时间轴结束的宽度值（距离课表左边缘）
   */
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