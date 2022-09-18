package com.mredrock.cyxbs.course.page.course.data

import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.lib.course.item.affair.IAffairData

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 20:41
 */
data class AffairData(
  val stuNum: String,
  override val week: Int, // 在哪一周
  override val hashDay: Int, // 星期数，星期一为 0
  override val beginLesson: Int,  // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
  override val period: Int, // 长度
  val id: Int, // 事务唯一 id
  val time: Int, // 提醒时间
  val title: String,
  val content: String,
) : IAffairData, ICourseData {
  
  companion object DIFF : DiffUtil.ItemCallback<AffairData>() {
    override fun areItemsTheSame(oldItem: AffairData, newItem: AffairData): Boolean {
      return oldItem.stuNum == newItem.stuNum
        && oldItem.week == newItem.week
        && oldItem.hashDay == newItem.hashDay
        && oldItem.beginLesson == newItem.beginLesson
        && oldItem.period == newItem.period
        && oldItem.id == newItem.id
    }
  
    override fun areContentsTheSame(oldItem: AffairData, newItem: AffairData): Boolean {
      return oldItem == newItem
    }
  }
}