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
) : AffairArgs {
  data class AffairDurationArgs(
    val week: Int, // 第几周
    val weekNum: Int, // 星期几，星期一 为 0
    val beginLesson: Int, // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
    val period: Int, // 长度
  ) : AffairArgs
}
