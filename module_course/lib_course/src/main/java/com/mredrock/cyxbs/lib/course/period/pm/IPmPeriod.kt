package com.mredrock.cyxbs.lib.course.period.pm

import com.mredrock.cyxbs.lib.course.item.IItem
import com.mredrock.cyxbs.lib.course.period.RowInclude

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:07
 */
interface IPmPeriod {
  fun addPmItem(item: IPmItem)
  fun isIncludePmPeriod(item: IItem): RowInclude
  fun isIncludePmPeriod(row: Int): Boolean
  fun forEachPm(block: (row: Int) -> Unit)
}