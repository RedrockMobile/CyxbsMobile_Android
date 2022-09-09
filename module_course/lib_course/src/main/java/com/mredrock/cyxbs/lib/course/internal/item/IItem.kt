package com.mredrock.cyxbs.lib.course.internal.item

import android.content.Context
import android.view.View
import com.mredrock.cyxbs.lib.course.internal.view.course.lp.ItemLayoutParams

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 13:07
 */
interface IItem {
  
  /**
   * 初始化即将添加进父布局的 View
   *
   * ## 注意
   * 该方法在每次 addView() 时调用，但你可以自己实现 view 的复用
   */
  fun initializeView(context: Context): View
  
  /**
   * [initializeView] 对象对应的 layoutParams，用于添加进父布局时使用
   */
  val lp: ItemLayoutParams
}