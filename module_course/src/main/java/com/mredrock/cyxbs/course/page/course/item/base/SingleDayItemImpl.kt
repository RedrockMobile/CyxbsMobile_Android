package com.mredrock.cyxbs.course.page.course.item.base

import com.mredrock.cyxbs.course.page.course.item.ISingleDayRank
import com.mredrock.cyxbs.lib.course.item.single.ISingleDayData
import com.mredrock.cyxbs.lib.course.item.single.SingleDayLayoutParams

/**
 * .
 *
 * @author 985892345
 * 2023/2/22 12:29
 */
abstract class SingleDayItemImpl : OpenDialogItemImpl(),
  ISingleDayRank, // 用于排序
  ISingleDayData // 单天 item 所需要的数据
{
  
  abstract override val lp: SingleDayLayoutParams
  
  abstract override val week: Int
  
  abstract override val rank: Int
  
  final override val weekNum: Int
    get() = lp.weekNum
  final override val startNode: Int
    get() = lp.startNode
  final override val length: Int
    get() = lp.length
}