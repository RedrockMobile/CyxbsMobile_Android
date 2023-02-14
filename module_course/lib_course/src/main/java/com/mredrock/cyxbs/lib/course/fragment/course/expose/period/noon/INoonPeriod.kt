package com.mredrock.cyxbs.lib.course.fragment.course.expose.period.noon

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:07
 */
interface INoonPeriod {
  
  /**
   * 遍历中午时间段区域
   */
  fun forEachNoon(block: (row: Int) -> Unit)
  
  /**
   * 跟中午时间段作对比
   * @return 1、返回正数，说明 [row] 在中午时间段下面；2、返回负数，说明在上面；3、返回 0，说明在里面
   */
  fun compareNoonPeriod(row: Int): Int
  
  /**
   * 得到中午时间段开始时的高度值（距离课表上边缘）
   */
  fun getNoonStartHeight(): Int
  
  /**
   * 得到中午时间段结束时的高度值（距离课表上边缘）
   */
  fun getNoonEndHeight(): Int
}