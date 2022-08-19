package com.mredrock.cyxbs.lib.course.internal.period.night

import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.period.RowInclude

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:07
 */
interface INightPeriod {
  fun isIncludeNightPeriod(item: IItem): RowInclude
  fun isIncludeNightPeriod(row: Int): Boolean
  fun forEachNight(block: (row: Int) -> Unit)
}