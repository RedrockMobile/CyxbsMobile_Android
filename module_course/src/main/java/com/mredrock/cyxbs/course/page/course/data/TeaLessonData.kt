package com.mredrock.cyxbs.course.page.course.data

import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.course.page.course.room.StuLessonEntity
import com.mredrock.cyxbs.course.page.course.room.TeaLessonEntity

/**
 * 参数解释请以 Room 数据库为准，请看 [StuLessonEntity]
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/10 21:15
 */
data class TeaLessonData(
  val teaNum: String,
  override val week: Int,
  override val beginLesson: Int,
  override val classroom: String,
  override val course: String,
  override val courseNum: String,
  override val day: String, // 星期几，这是字符串的星期几：星期一、星期二......
  override val hashDay: Int,
  override val period: Int,
  override val rawWeek: String,
  override val teacher: String,
  override val type: String,
  val classNumber: List<String>,
) : LessonData() {
  companion object {
    val DIFF_UTIL by lazy {
      object : DiffUtil.ItemCallback<TeaLessonData>() {
        override fun areItemsTheSame(oldItem: TeaLessonData, newItem: TeaLessonData): Boolean {
          return oldItem.teaNum == newItem.teaNum
            && oldItem.courseNum == newItem.courseNum
            && oldItem.week == newItem.week
            && oldItem.hashDay == newItem.hashDay
            && oldItem.beginLesson == newItem.beginLesson
        }
        
        override fun areContentsTheSame(oldItem: TeaLessonData, newItem: TeaLessonData): Boolean {
          return oldItem == newItem
        }
      }
    }
  }
}

fun List<TeaLessonEntity>.toTeaLessonData(): List<TeaLessonData> {
  return buildList {
    this@toTeaLessonData.forEach { entity ->
      entity.week.forEach { week ->
        add(
          TeaLessonData(
            entity.teaNum,
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
            entity.classNumber
          )
        )
      }
    }
  }
}
