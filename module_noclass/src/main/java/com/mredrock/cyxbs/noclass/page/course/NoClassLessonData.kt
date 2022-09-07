package com.mredrock.cyxbs.noclass.page.course

import com.mredrock.cyxbs.lib.course.internal.lesson.ILessonData

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.course
 * @ClassName:      NoClassLessonData
 * @Author:         Yan
 * @CreateDate:     2022年09月06日 17:25:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */
class NoClassLessonData(
  override val weekNum: Int,
  override val startNode: Int,
  override val length: Int,
  val names : String,
) : ILessonData {

  val timeType: Type
    get() {
      return when (startNode) {
        in 0 .. 3 -> Type.AM
        in 4 .. 4 -> Type.NOON
        in 5 .. 8 -> Type.PM
        in 9 .. 9 -> Type.DUSK
        in 10 .. 13 -> Type.NIGHT
        else -> throw RuntimeException("未知课程出现在未知时间段！bean = $this")
      }
    }
  
  enum class Type {
    AM, NOON ,PM, NIGHT, DUSK
  }
  
}