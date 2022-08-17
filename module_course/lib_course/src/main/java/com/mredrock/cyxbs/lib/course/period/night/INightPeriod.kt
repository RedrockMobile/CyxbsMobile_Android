package com.mredrock.cyxbs.lib.course.period.night

import com.mredrock.cyxbs.lib.course.item.IItem
import com.mredrock.cyxbs.lib.course.period.RowInclude

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:07
 */
interface INightPeriod {
  fun addNightItem(item: INightItem)
  fun isIncludeNightPeriod(item: IItem): RowInclude
  fun isIncludeNightPeriod(row: Int): Boolean
  fun forEachNight(block: (row: Int) -> Unit)
}