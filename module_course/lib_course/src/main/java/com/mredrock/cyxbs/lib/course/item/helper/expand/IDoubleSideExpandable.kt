package com.mredrock.cyxbs.lib.course.item.helper.expand

import android.view.View
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.item.touch.helper.expand.ExpandableItemHelper

/**
 * 双边扩展 Item 的帮助类接口
 *
 * 这个打算是用于双指触摸的，但因为已经有了 [ExpandableItemHelper] 就没写了，但这个双边扩展的功能是写好了的，就这样留着吧
 *
 * @author 985892345
 * 2023/4/19 21:17
 */
interface IDoubleSideExpandable : ISingleSideExpandable {
  
  /**
   * 双边移动时的回调
   *
   * ## 注意
   * - 如果存在 translationY，请调用方自己减去该值
   *
   * @param y1 相对于 course 的 y 值
   * @param y2 相对于 course 的 y 值
   */
  fun onDoubleMove(course: ICourseViewGroup, item: IItem, child: View, y1: Int, y2: Int)
}