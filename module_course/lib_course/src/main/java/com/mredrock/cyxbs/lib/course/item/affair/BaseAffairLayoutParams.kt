package com.mredrock.cyxbs.lib.course.item.affair

import com.mredrock.cyxbs.lib.course.item.single.SingleDayLayoutParams

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/19 14:05
 */
abstract class BaseAffairLayoutParams(
  weekNum: Int,
  startNode: Int,
  length: Int
) : SingleDayLayoutParams(
  weekNum, startNode, length
), IAffairData {
  
  constructor(data: IAffairData) : this(data.weekNum, data.startNode, data.length)
}