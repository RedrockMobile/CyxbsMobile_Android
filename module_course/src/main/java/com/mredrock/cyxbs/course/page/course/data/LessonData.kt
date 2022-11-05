package com.mredrock.cyxbs.course.page.course.data

import com.mredrock.cyxbs.lib.course.item.lesson.ILessonData
import com.mredrock.cyxbs.lib.course.item.lesson.LessonPeriod

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/10 21:13
 */
sealed class LessonData : ILessonData, ICourseData {
  abstract val num: String // 学号或工号
  abstract override val week: Int
  
  abstract val course: Course
  
  final override val hashDay: Int
    get() = course.hashDay
  final override val beginLesson: Int
    get() = course.beginLesson
  final override val period: Int
    get() = course.period
  
  val lessonPeriod: LessonPeriod
    get() {
      return when (beginLesson) {
        in 1 .. 4 -> LessonPeriod.AM
        in 5 .. 8 -> LessonPeriod.PM
        in 9 .. 12 -> LessonPeriod.NIGHT
        else -> throw RuntimeException("未知课程出现在未知时间段！bean = $this")
      }
    }
  
  /**
   * 属于同一节课的数据类，这个可以用于表示同一个老师上的同一个课程
   */
  data class Course(
    val course: String,
    val classroom: String,
    val courseNum: String,
    val hashDay: Int, // 星期数，星期一为 0
    val beginLesson: Int,
    val period: Int,
    val teacher: String,
    val rawWeek: String,
    val type: String
  )
}