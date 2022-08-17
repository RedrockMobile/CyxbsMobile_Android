package com.mredrock.cyxbs.lib.course.period.noon

import com.mredrock.cyxbs.lib.course.item.IItem
import com.mredrock.cyxbs.lib.course.period.RowInclude

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:07
 */
interface INoonPeriod {
  fun addNoonItem(item: INoonItem)
  fun isIncludeNoonPeriod(item: IItem): RowInclude
  fun isIncludeNoonPeriod(row: Int): Boolean
  fun forEachNoon(block: (row: Int) -> Unit)
}