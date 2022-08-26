package com.mredrock.cyxbs.lib.course.fragment.page.period.am

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:06
 */
interface IAmPeriod {
  fun isIncludeAmPeriod(row: Int): Boolean
  fun forEachAm(block: (row: Int) -> Unit)
  fun getAmStartHeight(): Int
  fun getAmEndHeight(): Int
}