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
  override val course: Course,
  val classNumber: List<String>,
) : LessonData() {
  companion object {
    val DIFF_UTIL by lazy {
      object : DiffUtil.ItemCallback<TeaLessonData>() {
        override fun areItemsTheSame(oldItem: TeaLessonData, newItem: TeaLessonData): Boolean {
          return oldItem.teaNum == newItem.teaNum
            && oldItem.course == newItem.course
            && oldItem.week == newItem.week
        }
        
        override fun areContentsTheSame(oldItem: TeaLessonData, newItem: TeaLessonData): Boolean {
          return oldItem == newItem
        }
      }
    }
  }
  
  override val num: String
    get() = teaNum
}

fun List<TeaLessonEntity>.toTeaLessonData(): List<TeaLessonData> {
  return buildList {
    this@toTeaLessonData.forEach { entity ->
      entity.week.forEach { week ->
        add(
          TeaLessonData(
            entity.teaNum,
            week,
            LessonData.Course(
              entity.course,
              entity.classroom,
              entity.courseNum,
              entity.hashDay,
              entity.beginLesson,
              entity.period,
              entity.teacher,
              entity.rawWeek,
              entity.type,
            ),
            entity.classNumber
          )
        )
      }
    }
  }
}
