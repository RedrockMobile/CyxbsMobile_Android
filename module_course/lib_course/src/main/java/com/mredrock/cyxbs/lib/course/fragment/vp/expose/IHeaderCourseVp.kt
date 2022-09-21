package com.mredrock.cyxbs.lib.course.fragment.vp.expose

import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 13:28
 */
interface IHeaderCourseVp : ICourseVp {
  
  /**
   * 头布局
   */
  val mHeader: ViewGroup
  
  /**
   * 这个不是显示 整学期 那个 View，而是旁边显示 ⌈（本周）> ⌋ 那个小 View
   */
  val mTvNowWeek: TextView
  
  /**
   * 显示 整学期、第一周。。。的 View
   */
  val mTvWhichWeek: TextView
  
  /**
   * 回到本周的按钮
   */
  val mBtnBackNowWeek: Button
  
}