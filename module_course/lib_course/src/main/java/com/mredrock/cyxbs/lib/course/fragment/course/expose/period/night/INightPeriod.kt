package com.mredrock.cyxbs.lib.course.fragment.course.expose.period.night

/**
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:07
 */
interface INightPeriod {
  
  /**
   * 遍历晚上时间段区域
   */
  fun forEachNight(block: (row: Int) -> Unit)
  
  /**
   * 跟晚上时间段作对比
   * @return 1、返回正数，说明 [row] 在晚上时间段下面；2、返回负数，说明在上面；3、返回 0，说明在里面
   */
  fun compareNightPeriod(row: Int): Int
  
  /**
   * 得到晚上时间段开始时的高度值
   */
  fun getNightStartHeight(): Int
  
  /**
   * 得到晚上时间段结束时的高度值
   */
  fun getNightEndHeight(): Int
}