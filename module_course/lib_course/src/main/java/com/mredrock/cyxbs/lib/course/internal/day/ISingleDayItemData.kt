package com.mredrock.cyxbs.lib.course.internal.day

import com.mredrock.cyxbs.lib.course.internal.item.ISingleColumnItemData

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/19 14:13
 */
interface ISingleDayItemData : ISingleColumnItemData {
  
  /**
   * 星期数，星期一为 1
   *
   * 你可能会很好奇为什么设置成星期一为 1，而不与网络请求的一样星期一为 0？
   * 原因在于：控件内部逻辑的装换，0 的位置给侧边的时间轴了
   */
  val weekNum: Int
  
  /**
   * 开始位置，从 0 开始
   * 这个不区分中午和傍晚时间段，0 就代表是第 0 行
   */
  val startNode: Int
  
  /**
   * 长度
   */
  val length: Int
  
  override val startRow: Int
    get() = startNode
  
  override val endRow: Int
    get() = startNode + length - 1
  
  @Deprecated("不建议调用", level = DeprecationLevel.HIDDEN)
  override val singleColumn: Int
    get() = weekNum
}