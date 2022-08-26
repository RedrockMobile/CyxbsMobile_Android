package com.mredrock.cyxbs.lib.course.fragment.page.period.night

/**
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:07
 */
interface INightPeriod {
  fun isIncludeNightPeriod(row: Int): Boolean
  fun forEachNight(block: (row: Int) -> Unit)
  fun getNightStartHeight(): Int
  fun getNightEndHeight(): Int
}