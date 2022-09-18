package com.mredrock.cyxbs.lib.course.item.single

import android.view.Gravity
import com.mredrock.cyxbs.lib.course.internal.view.course.lp.ItemLayoutParams

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/9 18:57
 */
open class SingleDayLayoutParams(
  weekNum: Int,
  startNode: Int,
  length: Int,
  width: Int = MATCH_PARENT,
  height: Int = MATCH_PARENT,
  gravity: Int = Gravity.CENTER
) : ItemLayoutParams(
  startNode,
  startNode + length - 1,
  weekNum,
  weekNum,
  width,
  height,
  gravity
), ISingleDayData
{
  
  val singleColumn: Int
    get() = startColumn
  
  final override val weekNum: Int
    get() = startColumn
  final override val startNode: Int
    get() = startRow
  final override val length: Int
    get() = rowCount
  
  /**
   * 根据 [ISingleDayData] 改变数据
   */
  fun changeSingleDay(data: ISingleDayData) {
    startColumn = data.weekNum
    endColumn = data.weekNum
    startRow = data.startNode
    endRow = data.startNode + data.length - 1
  }
}