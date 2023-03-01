package com.mredrock.cyxbs.lib.course.fragment.course.expose.period.dusk

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:07
 */
interface IDuskPeriod {
  
  /**
   * 遍历傍晚时间段区域
   */
  fun forEachDusk(block: (row: Int) -> Unit)
  
  /**
   * 跟傍晚时间段作对比
   * @return 1、返回正数，说明 [row] 在傍晚时间段下面；2、返回负数，说明在上面；3、返回 0，说明在里面
   */
  fun compareDuskPeriodByRow(row: Int): Int
  
  /**
   * 同 [compareDuskPeriodByRow]，但比较的是高度 [height]
   */
  fun compareDuskPeriodByHeight(height: Int): Int
  
  /**
   * 得到傍晚时间段开始时的高度值（距离课表上边缘）
   */
  fun getDuskStartHeight(): Int
  
  /**
   * 得到傍晚时间段结束时的高度值（距离课表上边缘）
   */
  fun getDuskEndHeight(): Int
}