package com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap

import com.mredrock.cyxbs.lib.course.item.overlap.IOverlap
import com.mredrock.cyxbs.lib.course.item.overlap.IOverlapItem

/**
 * 负责 [IOverlapItem] 的容器
 *
 * ## 注意
 * - 如果你遇到了重新布局时只有 Item 出现卡顿的情况，可以看看 [IOverlap.lockRefreshAnim]
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/8 12:17
 */
interface IOverlapContainer {
  
  /**
   * 比较某一格位置的重叠顺序
   * @return > 0，则 o1 显示在 o2 上（只允许是同一个对象时返回 0）
   */
  fun compareOverlayItem(row: Int, column: Int, o1: IOverlapItem, o2: IOverlapItem): Int
  
  /**
   * 改变重叠状态
   */
  fun changeOverlap(item: IOverlapItem?, isOverlap: Boolean): Boolean
  
  /**
   * 刷新所有 Item 的重叠状态
   * @param itemsWithoutAnim 不执行刷新动画的 item，因为动画会抑制布局，在改变 View 位置时会修改失败
   */
  fun refreshOverlap(itemsWithoutAnim: List<IOverlapItem> = emptyList())
}