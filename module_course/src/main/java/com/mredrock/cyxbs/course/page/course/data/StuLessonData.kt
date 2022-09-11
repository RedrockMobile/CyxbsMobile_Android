package com.mredrock.cyxbs.course.page.course.data

import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.course.page.course.room.StuLessonEntity

/**
 * 参数解释请以 Room 数据库为准，请看 [StuLessonEntity]
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/10 21:13
 */
data class StuLessonData(
  val stuNum: String,
  override val week: Int,
  override val beginLesson: Int,
  override val classroom: String,
  override val course: String,
  override val courseNum: String,
  override val day: String, // 星期几，这是字符串的星期几：星期一、星期二......
  override val hashDay: Int, // 星期数，星期一为 0
  override val period: Int,
  override val rawWeek: String,
  override val teacher: String,
  override val type: String,
) : LessonData() {
  companion object : DiffUtil.ItemCallback<StuLessonData>() {
    override fun areItemsTheSame(oldItem: StuLessonData, newItem: StuLessonData): Boolean {
      return oldItem.stuNum == newItem.stuNum
        && oldItem.classroom == newItem.classroom
        && oldItem.courseNum == newItem.courseNum
        && oldItem.week == newItem.week
    }
  
    override fun areContentsTheSame(oldItem: StuLessonData, newItem: StuLessonData): Boolean {
      return oldItem == newItem
    }
  }
}

fun List<StuLessonEntity>.toStuLessonData() : List<StuLessonData> {
  return buildList {
    this@toStuLessonData.forEach { entity ->
      entity.week.forEach { week ->
        add(
          StuLessonData(
            entity.stuNum,
            week,
            entity.beginLesson,
            entity.classroom,
            entity.course,
            entity.courseNum,
            entity.day,
            entity.hashDay,
            entity.period,
            entity.rawWeek,
            entity.teacher,
            entity.type,
          )
        )
      }
    }
  }
}