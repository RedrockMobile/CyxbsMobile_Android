package com.mredrock.cyxbs.course.page.course.data

import com.mredrock.cyxbs.lib.course.item.lesson.ILessonData
import com.mredrock.cyxbs.lib.course.item.lesson.LessonPeriod

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/10 21:13
 */
/**
 * 暂时去掉密封类的使用，待R8完全支持后请改回来，原因：
 * 密封类在 D8 和 R8 编译器（产生错误的编译器）中不完全受支持。
 * 在 https://issuetracker.google.com/227160052 中跟踪完全支持的密封类。
 * D8支持将出现在Android Studio Electric Eel中，目前处于预览状态，而R8支持在更高版本之前不会出现
 * stackoverflow上的回答：
 * https://stackoverflow.com/questions/73453524/what-is-causing-this-error-com-android-tools-r8-internal-nc-sealed-classes-are#:~:text=This%20is%20due%20to%20building%20using%20JDK%2017,that%20the%20dexer%20won%27t%20be%20able%20to%20handle.
 */
abstract class LessonData : ILessonData, ICourseItemData {
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