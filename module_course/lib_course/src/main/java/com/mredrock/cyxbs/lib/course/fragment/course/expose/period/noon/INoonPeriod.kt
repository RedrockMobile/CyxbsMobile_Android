package com.mredrock.cyxbs.lib.course.fragment.course.expose.period.noon

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:07
 */
interface INoonPeriod {
  fun isIncludeNoonPeriod(row: Int): Boolean
  fun forEachNoon(block: (row: Int) -> Unit)
  
  /**
   * 得到中午时间段开始时的高度值
   */
  fun getNoonStartHeight(): Int
  
  /**
   * 得到中午时间段结束时的高度值
   */
  fun getNoonEndHeight(): Int
}