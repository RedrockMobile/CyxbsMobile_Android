package com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/8 12:17
 */
interface IOverlapContainer {
  
  /**
   * 比较某一格位置的重叠顺序
   * @return > 0，则 o1 显示在 o2 上；= 0，则 o1 会替换 o2（只允许是同一个对象时返回 0）
   */
  fun compareOverlayItem(row: Int, column: Int, o1: IOverlapItem, o2: IOverlapItem): Int
}