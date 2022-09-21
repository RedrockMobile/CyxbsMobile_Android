package com.mredrock.cyxbs.course.page.course.data

import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.course.page.course.room.ILessonEntity
import com.mredrock.cyxbs.course.page.course.room.StuLessonEntity

/**
 * 参数解释请以 Room 数据库为准，请看 [ILessonEntity]
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/10 21:13
 */
data class StuLessonData(
  val stuNum: String,
  override val week: Int,
  override val course: Course,
) : LessonData() {
  companion object : DiffUtil.ItemCallback<StuLessonData>() {
    override fun areItemsTheSame(oldItem: StuLessonData, newItem: StuLessonData): Boolean {
      return oldItem.stuNum == newItem.stuNum
        && oldItem.course == newItem.course
        && oldItem.week == newItem.week
    }
  
    override fun areContentsTheSame(oldItem: StuLessonData, newItem: StuLessonData): Boolean {
      return oldItem == newItem
    }
  }
  
  override val num: String
    get() = stuNum
}

fun List<StuLessonEntity>.toStuLessonData() : List<StuLessonData> {
  return buildList {
    this@toStuLessonData.forEach { entity ->
      entity.week.forEach { week ->
        add(
          StuLessonData(
            entity.stuNum,
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
            )
          )
        )
      }
    }
  }
}