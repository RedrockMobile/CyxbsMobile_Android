package com.mredrock.cyxbs.lib.course.item.touch.helper.longpress

import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * 长按触摸事件的监听
 *
 * - 只会回调触发长按的那根手指的事件
 *
 * @author 985892345
 * 2023/4/19 13:04
 */
interface ILongPressItemListener {
  
  /**
   * DOWN 事件
   */
  fun onDown(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    event: IPointerEvent
  ) {}
  
  /**
   * 长按激活时回调
   * @param x 相对于 course 的 x 值
   * @param y 相对于 course 的 y 值
   */
  fun onLongPressed(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    x: Int,
    y: Int,
    pointerId: Int
  ) {}
  
  /**
   * 长按因为移动距离过大而被取消
   */
  fun onLongPressCancel(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    event: IPointerEvent
  ) {}
  
  /**
   * 手指移动
   *
   * 该方法前提条件是 [onLongPressed] 已经被回调，即长按已经触发
   *
   * 包含下面几种情况：
   * - 手指移动
   * - 滚轴移动
   * - 展开中午或者傍晚时间段
   * @param x 相对于 course 的 x 值
   * @param y 相对于 course 的 y 值
   */
  fun onMove(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    x: Int,
    y: Int,
  ) {}
  
  /**
   * 触摸事件结束手指抬起或者 CANCEL
   *
   * @param isInLongPress true -> 处于长按激活状态；false -> 长按还未激活；null -> 长按激活前移动距离过大被取消长按
   */
  fun onEventEnd(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    event: IPointerEvent,
    isInLongPress: Boolean?,
    isCancel: Boolean
  ) {}
}