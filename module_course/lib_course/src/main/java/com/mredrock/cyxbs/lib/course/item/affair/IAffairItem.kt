package com.mredrock.cyxbs.lib.course.item.affair

import com.mredrock.cyxbs.lib.course.item.single.ISingleDayItem

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 13:03
 */
interface IAffairItem : ISingleDayItem, IAffairData {
  
  /**
   * 正确继承写法：
   * ```
   * override val lp = BaseAffairLayoutParams(data)
   * ```
   */
  override val lp: BaseAffairLayoutParams
}