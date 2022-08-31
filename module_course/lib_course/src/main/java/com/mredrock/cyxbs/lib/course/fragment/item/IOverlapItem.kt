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
   * [position] 位置是否显示
   */
  fun isShow(position: Int): Boolean
  
  /**
   * 被重叠时的回调
   * @param position 自己的第 [position] 的位置被重叠
   */
  fun onOverlapped(position: Int, item: IOverlapItem?)
  
  /**
   * 重叠了其他 item 时的回调
   * @param position 自己的第 [position] 的位置重叠了另一个 item
   */
  fun onOverlapping(position: Int, item: IOverlapItem?)
  
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
   * 得到 [position] 位置重叠在上面的 item
   */
  fun getAboveItem(position: Int): IOverlapItem?
  
  /**
   * 得到 [position] 位置重叠在下面的 item
   */
  fun getBelowItem(position: Int): IOverlapItem?
}