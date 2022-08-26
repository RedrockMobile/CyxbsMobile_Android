package com.mredrock.cyxbs.lib.course.fragment.page.period.dusk

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:07
 */
interface IDuskPeriod {
  fun isIncludeDuskPeriod(row: Int): Boolean
  fun forEachDusk(block: (row: Int) -> Unit)
  
  /**
   * 得到傍晚时间段开始时的高度值
   */
  fun getDuskStartHeight(): Int
  
  /**
   * 得到傍晚时间段结束时的高度值
   */
  fun getDuskEndHeight(): Int
}