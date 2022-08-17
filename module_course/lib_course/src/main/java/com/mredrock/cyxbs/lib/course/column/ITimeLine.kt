package com.mredrock.cyxbs.lib.course.column

import com.mredrock.cyxbs.lib.course.item.IItem
import com.mredrock.cyxbs.lib.course.period.ColumnInclude
import com.mredrock.cyxbs.lib.course.period.am.IAmPeriod
import com.mredrock.cyxbs.lib.course.period.dusk.IDuskPeriod
import com.mredrock.cyxbs.lib.course.period.night.INightPeriod
import com.mredrock.cyxbs.lib.course.period.noon.INoonPeriod
import com.mredrock.cyxbs.lib.course.period.pm.IPmPeriod

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 15:46
 */
interface ITimeLine : IColumn, IAmPeriod, INoonPeriod, IPmPeriod, IDuskPeriod, INightPeriod {
  
  fun isIncludeTimeLine(item: IItem): ColumnInclude
  
  fun isIncludeTimeLine(row: Int): Boolean
  
  fun forEachTimeLine(block: (row: Int) -> Unit)
  
  /**
   * 是否显示当前时间线
   */
  fun setIsShowNowTimeLine(boolean: Boolean)
  
  fun getTimeLineStartWidth(): Int
  
  fun getTimeLineEndWidth(): Int
}