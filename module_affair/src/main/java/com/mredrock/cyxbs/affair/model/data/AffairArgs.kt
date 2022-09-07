package com.mredrock.cyxbs.affair.model.data

import java.io.Serializable

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/11 16:39
 */
sealed interface AffairArgs : Serializable

data class AffairEditArgs(
  val stuNum: String,
  val id: Int,
  val title: String,
  val content: String,
  val remindTime: Int,
  val durations: AffairDurationArgs
) : Serializable, AffairArgs {
  data class AffairDurationArgs(
    val week: Int, // 第几周
    val weekNum: Int, // 星期几，星期一 为 0
    val beginLesson: Int, // 开始节数
    val period: Int, // 长度
  ) : Serializable, AffairArgs {
    fun getDurationStr(): String {
      val start = when (val it = beginLesson) {
        in 1..4 -> it - 1
        in 5..8 -> it
        in 9..12 -> it + 1
        -1 -> 4 // 中午
        -2 -> 9 // 傍晚
        else -> throw RuntimeException("未知时间段")
      }
      return if (period == 1) {
        "${week}周 ${WEEK_ARRAY[weekNum]} ${LESSON_ARRAY[start]}"
      } else {
        "${week}周 ${WEEK_ARRAY[weekNum]} ${LESSON_ARRAY[start]}-${LESSON_ARRAY[start + period - 1]}"
      }
    }

    companion object {
      val LESSON_ARRAY = arrayOf(
        "第一节课", "第二节课", "第三节课", "第四节课", "中午",
        "第五节课", "第六节课", "第七节课", "第八节课", "傍晚",
        "第九节课", "第十节课", "第十一节课", "第十二节课",
      )
      val WEEK_ARRAY = arrayOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    }
  }
}

fun AffairData.toAffairEditArgs(): AffairEditArgs {
  return AffairEditArgs(stuNum, id, title, content, time, toAffairDurationArgs())
}

fun AffairData.toAffairDurationArgs(): AffairEditArgs.AffairDurationArgs {
  return AffairEditArgs.AffairDurationArgs(week, day, beginLesson, period)
}
