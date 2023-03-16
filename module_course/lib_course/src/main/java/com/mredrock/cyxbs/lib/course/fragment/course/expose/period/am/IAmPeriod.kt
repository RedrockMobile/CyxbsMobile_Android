package com.mredrock.cyxbs.lib.course.fragment.course.expose.period.am

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:06
 */
interface IAmPeriod {
  
  /**
   * 遍历上午时间段区域
   */
  fun forEachAm(block: (row: Int) -> Unit)
  
  /**
   * 跟上午时间段作对比
   * @return 1、返回正数，说明 [row] 在上午时间段下面；2、返回负数，说明在上面；3、返回 0，说明在里面
   */
  fun compareAmPeriodByRow(row: Int): Int
  
  /**
   * 同 [compareAmPeriodByRow]，但比较的是高度 [height]
   */
  fun compareAmPeriodByHeight(height: Int): Int
  
  /**
   * 得到上午时间段开始时的高度值（距离课表上边缘）
   */
  fun getAmStartHeight(): Int
  
  /**
   * 得到上午时间段结束时的高度值（距离课表上边缘）
   */
  fun getAmEndHeight(): Int
}