package com.mredrock.cyxbs.lib.course.internal.period.dusk

import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.period.RowInclude

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:07
 */
interface IDuskPeriod {
  fun isIncludeDuskPeriod(item: IItem): RowInclude
  fun isIncludeDuskPeriod(row: Int): Boolean
  fun forEachDusk(block: (row: Int) -> Unit)
}