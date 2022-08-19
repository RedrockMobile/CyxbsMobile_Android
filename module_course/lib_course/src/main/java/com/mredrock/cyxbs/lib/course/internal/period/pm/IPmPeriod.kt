package com.mredrock.cyxbs.lib.course.internal.period.pm

import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.period.RowInclude

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:07
 */
interface IPmPeriod {
  fun isIncludePmPeriod(item: IItem): RowInclude
  fun isIncludePmPeriod(row: Int): Boolean
  fun forEachPm(block: (row: Int) -> Unit)
}