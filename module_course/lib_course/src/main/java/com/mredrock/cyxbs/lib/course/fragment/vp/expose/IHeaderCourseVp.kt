package com.mredrock.cyxbs.lib.course.fragment.vp.expose

import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.tabs.TabLayout

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 13:28
 */
interface IHeaderCourseVp : ICourseVp {
  
  /**
   * 头布局，包含 content 和 tab
   */
  val mHeader: ViewGroup

  /**
   * 头布局中的 content
   */
  val mHeaderContent: ViewGroup

  /**
   * 头布局中的 tab，其中包含 [mTabLayout]
   */
  val mHeaderTab: ViewGroup
  
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

  /**
   * 点击 [mTvWhichWeek] 后显示 TabLayout
   */
  val mTabLayout: TabLayout

  /**
   * 退出显示 TabLayout 的按钮
   */
  val mIvTableBack: ImageView
}