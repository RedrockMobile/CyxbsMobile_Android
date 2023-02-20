package com.mredrock.cyxbs.lib.course.item.touch.helper.move

import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem

/**
 * .
 *
 * @author 985892345
 * 2023/2/20 11:19
 */
interface IMovableListener {
  
  /**
   * DOWN 事件
   */
  fun onDown(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    initialX: Int,
    initialY: Int
  ) {
  }
  
  /**
   * 长按激活时回调
   */
  fun onLongPressStart(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    initialX: Int,
    initialY: Int,
    x: Int,
    y: Int
  ) {
  }
  
  /**
   * 长按因为移动距离过大而被取消
   */
  fun onLongPressCancel(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    initialX: Int,
    initialY: Int,
    x: Int,
    y: Int
  ) {
  }
  
  /**
   * item 移动
   *
   * 包含下面几中情况：
   * - 手指移动
   * - 滚轴移动
   * - 展开中午或者傍晚时间段
   */
  fun onMove(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    initialX: Int,
    initialY: Int,
    x: Int,
    y: Int
  ) {
  }
  
  /**
   * 手指抬起
   *
   * @param isInLongPress true -> 处于长按激活状态；false -> 长按还未激活；null -> 长按激活前移动距离过大被取消长按
   */
  fun onUp(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    initialX: Int,
    initialY: Int,
    x: Int,
    y: Int,
    isInLongPress: Boolean?
  ) {
  }
  
  /**
   * 事件分发被取消
   *
   * @param isInLongPress true -> 处于长按激活状态；false -> 长按还未激活；null -> 长按激活前移动距离过大被取消长按
   */
  fun onCancel(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    initialX: Int,
    initialY: Int,
    x: Int,
    y: Int,
    isInLongPress: Boolean?
  ) {
  }
  
  /**
   * 结束动画开始
   *
   * @param newLocation 新的位置。如果为 null，则回到原位置
   * @param fraction 动画进度，0 -> 1
   */
  fun onOverAnimStart(
    newLocation: LocationUtil.Location?,
    page: ICoursePage,
    item: ITouchItem,
    child: View,
  ) {
  }
  
  /**
   * 结束动画执行中
   *
   * @param newLocation 新的位置。如果为 null，则回到原位置
   * @param fraction 动画进度，0 -> 1
   */
  fun onOverAnimUpdate(
    newLocation: LocationUtil.Location?,
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    fraction: Float
  ) {
  }
  
  /**
   * 结束动画结束
   *
   * @param newLocation 新的位置。如果为 null，则回到原位置
   */
  fun onOverAnimEnd(
    newLocation: LocationUtil.Location?,
    page: ICoursePage,
    item: ITouchItem,
    child: View,
  ) {
  }
}