package lib.course.data

import com.mredrock.cyxbs.lib.course.internal.lesson.ILessonData

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/26 17:48
 */
data class LessonData(
  val week: Int,
  val beginLesson: Int,
  val period: Int, // 星期数，星期一为 0
  val hashDay: Int,
  val course: String,
  val classroom: String,
) : ILessonData {
  
  override val weekNum: Int
    get() = hashDay + 1
  
  override val startNode: Int
    get() = when (beginLesson) {
      in 1..4 -> beginLesson - 1
      in 5..8 -> beginLesson
      in 9..12 -> beginLesson + 1
      -1 -> 4 // 中午
      -2 -> 9 // 傍晚
      else -> throw RuntimeException("出现了未知的时间点！")
    }
    
  override val length: Int
    get() = period
  
  val timeType: Type
    get() {
      return when (beginLesson) {
        in 1 .. 4 -> Type.AM
        in 5 .. 8 -> Type.PM
        in 9 .. 12 -> Type.NIGHT
        else -> throw RuntimeException("未知课程出现在未知时间段！bean = $this")
      }
    }
  
  enum class Type {
    // 暂不存在中午时间段的课
    AM, PM, NIGHT
  }
}