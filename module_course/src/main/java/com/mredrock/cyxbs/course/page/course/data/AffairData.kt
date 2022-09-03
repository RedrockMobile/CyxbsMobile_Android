package com.mredrock.cyxbs.course.page.course.data

import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.lib.course.internal.affair.IAffairData

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 20:41
 */
data class AffairData(
  val stuNum: String,
  val id: Int, // 事务唯一 id
  val time: Int, // 提醒时间
  val title: String,
  val content: String,
  override val week: Int, // 在哪一周
  val beginLesson: Int,  // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
  val day: Int, // 星期数，星期一为 0
  val period: Int, // 长度
) : IAffairData, IWeek {
  
  override val weekNum: Int
    get() = day + 1
  override val startNode: Int
    get() = when (val it = beginLesson) {
      in 1..4 -> it - 1
      in 5..8 -> it
      in 9..12 -> it + 1
      -1 -> 4 // 中午
      -2 -> 9 // 傍晚
      else -> it // 不存在其他时间段的事务，但为了以防万一
    }
  override val length: Int
    get() = period
}