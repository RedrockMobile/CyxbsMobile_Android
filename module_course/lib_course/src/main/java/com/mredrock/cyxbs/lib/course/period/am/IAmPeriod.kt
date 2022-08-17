package com.mredrock.cyxbs.lib.course.period.am

import com.mredrock.cyxbs.lib.course.item.IItem
import com.mredrock.cyxbs.lib.course.period.RowInclude

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:06
 */
interface IAmPeriod {
  fun addAmItem(item: IAmItem)
  fun isIncludeAmPeriod(item: IItem): RowInclude
  fun isIncludeAmPeriod(row: Int): Boolean
  fun forEachAm(block: (row: Int) -> Unit)
}