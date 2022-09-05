package com.mredrock.cyxbs.lib.course.fragment.course.expose.period.pm

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:07
 */
interface IPmPeriod {
  fun isIncludePmPeriod(row: Int): Boolean
  fun forEachPm(block: (row: Int) -> Unit)
  fun getPmStartHeight(): Int
  fun getPmEndHeight(): Int
}