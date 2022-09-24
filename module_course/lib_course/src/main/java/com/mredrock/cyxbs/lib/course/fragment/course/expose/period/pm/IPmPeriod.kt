package com.mredrock.cyxbs.lib.course.fragment.course.expose.period.pm

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:07
 */
interface IPmPeriod {
  
  /**
   * 遍历下午时间段区域
   */
  fun forEachPm(block: (row: Int) -> Unit)
  
  /**
   * 跟下午时间段作对比
   * @return 1、返回正数，说明 [row] 在下午时间段下面；2、返回负数，说明在上面；3、返回 0，说明在里面
   */
  fun comparePmPeriod(row: Int): Int
  
  /**
   * 得到下午时间段开始时的高度值
   */
  fun getPmStartHeight(): Int
  
  /**
   * 得到下午时间段结束时的高度值
   */
  fun getPmEndHeight(): Int
}