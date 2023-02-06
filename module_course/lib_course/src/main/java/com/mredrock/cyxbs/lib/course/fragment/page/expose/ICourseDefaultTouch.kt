package com.mredrock.cyxbs.lib.course.fragment.page.expose

import com.mredrock.cyxbs.lib.course.helper.show.CourseDownAnimDispatcher
import com.mredrock.cyxbs.lib.course.internal.touch.IMultiTouch

/**
 * 该接口与默认添加的触摸事件帮助类有关
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 18:23
 */
interface ICourseDefaultTouch : IMultiTouch.DefaultHandler {
  /**
   * 得到点击 View 后 Q 弹动画的事件帮助类
   */
  fun getCourseDownAnimDispatcher(): CourseDownAnimDispatcher?
}