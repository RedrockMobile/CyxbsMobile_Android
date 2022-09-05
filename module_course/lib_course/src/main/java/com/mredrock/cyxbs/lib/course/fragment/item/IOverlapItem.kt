 package com.mredrock.cyxbs.lib.course.fragment.item

import android.content.Context
import com.mredrock.cyxbs.lib.course.internal.day.ISingleDayItem

/**
 * 支持重叠的 item
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 17:30
 */
interface IOverlapItem : ISingleDayItem, Comparable<IOverlapItem> {
  
  /**
   * [row] 位置是否显示
   */
  fun isDisplayable(row: Int): Boolean
  
  /**
   * 被重叠时的回调
   * @param row 自己的第 [row] 的位置被重叠
   */
  fun onAboveItem(row: Int, item: IOverlapItem?)
  
  /**
   * 重叠了其他 item 时的回调
   * @param row 自己的第 [row] 的位置重叠了另一个 item
   */
  fun onBelowItem(row: Int, item: IOverlapItem?)
  
  /**
   * 即将被移除时的回调
   *
   * 这里需要清除掉所有重叠的引用
   */
  fun clearOverlap()
  
  /**
   * 是否允许添加到课表上
   *
   * 这个回调是在把所有 Item 都添加完后的一个 Runnable 中调用的
   */
  fun isAllowToAddIntoCourse(context: Context): Boolean
  
  /**
   * 得到 [row] 位置重叠在上面的 item
   */
  fun getAboveItem(row: Int): IOverlapItem?
  
  /**
   * 得到 [row] 位置重叠在下面的 item
   */
  fun getBelowItem(row: Int): IOverlapItem?
  
  /**
   * 用于比较处于同一格时的前后顺序
   */
  override fun compareTo(other: IOverlapItem): Int
}