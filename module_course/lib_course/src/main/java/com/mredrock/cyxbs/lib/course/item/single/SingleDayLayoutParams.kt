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
  override val weekNum: Int
    get() = startColumn
  override val startNode: Int
    get() = startRow
  override val length: Int
    get() = rowCount
}