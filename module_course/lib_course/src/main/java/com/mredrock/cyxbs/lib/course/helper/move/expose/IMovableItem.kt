package com.mredrock.cyxbs.lib.course.helper.move.expose

import android.view.View
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/8 7:46
 */
interface IMovableItem : IItem {
  
  /**
   * 父布局
   */
  val parent: ICourseViewGroup
  
  /**
   * 取消自身 View 的显示
   */
  fun cancelShowView()
  
  /**
   * 得到用于移动的 View
   *
   * ## 注意
   * - 该方法返回的 View 可以不用返回 [IItem.view]，返回其他 View 也是可行的
   */
  fun createMovableView(): View
}