package com.mredrock.cyxbs.lib.course.internal.view.course

/**
 * 课表滚轴的操控者，实现该接口将拥有操控课表滚轴的功能
 *
 * 由 ScrollView 实现功能，但 [ICourseViewGroup] 暴露该接口用于间接操控 ScrollView
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 14:12
 */
interface ICourseScrollControl {
  
  /**
   * 移动滚轴，注意坐标系的改变
   * @param dy 差值，> 0 时向上移动
   */
  fun scrollCourseBy(dy: Int)
  
  /**
   * 移动滚轴，注意坐标系的改变
   * @param y 向上滚出的距离
   */
  fun scrollCourseY(y: Int)
  
  /**
   * 得到课表移动的 ScrollY
   */
  fun getScrollCourseY(): Int
  
  /**
   * 得到 [pointerId] 对应的在滚轴上的 Y 值
   */
  fun getAbsoluteY(pointerId: Int): Int
  
  /**
   * 得到滚轴的高度
   */
  fun getScrollHeight(): Int
  
  /**
   * 得到对应方向上能否继续滑动
   * @param direction 正值检查手指向上滑动，负值检查手指向下滑动
   * ```
   *                                       true               false
   * canCourseScrollVertically(1)     手指能够向上滑动       手指不能向上滑动
   * canCourseScrollVertically(-1)    手指能够向下滑动       手指不能向下滑动
   * ```
   */
  fun canCourseScrollVertically(direction: Int): Boolean
}