package com.mredrock.cyxbs.lib.course.internal.period.am

import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.period.RowInclude

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:06
 */
interface IAmPeriod {
  fun isIncludeAmPeriod(item: IItem): RowInclude
  fun isIncludeAmPeriod(row: Int): Boolean
  fun forEachAm(block: (row: Int) -> Unit)
}