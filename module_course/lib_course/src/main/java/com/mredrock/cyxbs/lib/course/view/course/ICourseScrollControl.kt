package com.mredrock.cyxbs.lib.course.view.course

/**
 * 课表滚轴的操控者，实现该接口将拥有操控课表滚轴的功能
 *
 * 由 ScrollView 实现功能，但 CourseLayout 暴露该接口用于间接操控 ScrollView
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
}