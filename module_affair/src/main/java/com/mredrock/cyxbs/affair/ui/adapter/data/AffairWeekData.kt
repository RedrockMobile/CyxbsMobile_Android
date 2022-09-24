package com.mredrock.cyxbs.affair.ui.adapter.data

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/12 21:12
 */
sealed interface AffairAdapterData {
  abstract override fun equals(other: Any?): Boolean
  val onlyId: Any
}

data class AffairWeekData(
  val week: Int,
  val list: List<AffairTimeData>, //为了以后拓展

) : AffairAdapterData {
  override val onlyId: Any
    get() = week
}

data class AffairTimeData(
  val weekNum: Int, // 星期几，星期一 为 0
  val beginLesson: Int, // 开始节数
  val period: Int, // 长度
  val isWrapBefore: Boolean = false
) : AffairAdapterData {
  override val onlyId: Any
    get() = weekNum * 100 + beginLesson * 10 + period
}

data class AffairTimeAdd(val a: Int) : AffairAdapterData {
  override val onlyId: Any
    get() = 0
}

data class AffairWeekSelectData(val week: Int, var isChoice: Boolean = false)