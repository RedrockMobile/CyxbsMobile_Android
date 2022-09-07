package com.mredrock.cyxbs.course2.page.affair.ui.adapter.data

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/12 21:12
 */
data class AffairAdapterData(
  val week: Int,
  val list: List<AffairDurationData>
) {
  data class AffairDurationData(
    val weekNum: Int, // 星期几，星期一 为 0
    val beginLesson: Int, // 开始节数
    val period: Int, // 长度
  )
}