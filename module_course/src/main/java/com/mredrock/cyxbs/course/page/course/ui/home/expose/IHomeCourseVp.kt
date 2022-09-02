package com.mredrock.cyxbs.course.page.course.ui.home.expose

import android.widget.ImageView
import com.mredrock.cyxbs.lib.course.fragment.vp.expose.IHeaderCourseVp

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 15:21
 */
interface IHomeCourseVp : IHeaderCourseVp {
  
  /**
   * 用于显示关联图标 ImageView
   */
  val mIvLink: ImageView
  
  /**
   * 是否正显示关联人的课
   *
   * @return 返回 null 说明控件为 Gone 状态，即不可见也不可能点击
   */
  fun isShowingDoubleLesson(): Boolean?
  
  /**
   * 显示双人图标
   */
  fun showDoubleLink()
  
  /**
   * 显示单人图标
   */
  fun showSingleLink()
}