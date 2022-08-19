package com.mredrock.cyxbs.lib.course.internal.timeline

import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.period.ColumnInclude
import com.mredrock.cyxbs.lib.course.internal.period.am.IAmPeriod
import com.mredrock.cyxbs.lib.course.internal.period.dusk.IDuskPeriod
import com.mredrock.cyxbs.lib.course.internal.period.night.INightPeriod
import com.mredrock.cyxbs.lib.course.internal.period.noon.INoonPeriod
import com.mredrock.cyxbs.lib.course.internal.period.pm.IPmPeriod

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 15:46
 */
interface ITimeLine : IAmPeriod, INoonPeriod, IPmPeriod, IDuskPeriod, INightPeriod {
  
  fun isIncludeTimeLine(item: IItem): ColumnInclude
  
  fun isIncludeTimeLine(row: Int): Boolean
  
  fun forEachTimeLine(block: (row: Int) -> Unit)
  
  fun getTimeLineStartWidth(): Int
  
  fun getTimeLineEndWidth(): Int
}