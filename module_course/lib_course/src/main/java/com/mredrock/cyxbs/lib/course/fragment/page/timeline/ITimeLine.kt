package com.mredrock.cyxbs.lib.course.fragment.page.timeline

import com.mredrock.cyxbs.lib.course.fragment.page.period.am.IAmPeriod
import com.mredrock.cyxbs.lib.course.fragment.page.period.dusk.IDuskPeriod
import com.mredrock.cyxbs.lib.course.fragment.page.period.night.INightPeriod
import com.mredrock.cyxbs.lib.course.fragment.page.period.noon.INoonPeriod
import com.mredrock.cyxbs.lib.course.fragment.page.period.pm.IPmPeriod

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 15:46
 */
interface ITimeLine : IAmPeriod, INoonPeriod, IPmPeriod, IDuskPeriod, INightPeriod {
  
  fun isIncludeTimeLine(row: Int): Boolean
  
  fun forEachTimeLine(block: (column: Int) -> Unit)
  
  fun getTimeLineStartWidth(): Int
  
  fun getTimeLineEndWidth(): Int
}