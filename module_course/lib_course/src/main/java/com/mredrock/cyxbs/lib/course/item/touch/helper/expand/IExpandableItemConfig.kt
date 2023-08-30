package com.mredrock.cyxbs.lib.course.item.touch.helper.expand

import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.helper.longpress.ILongPressItemConfig
import com.mredrock.cyxbs.lib.course.item.touch.helper.expand.utils.DefaultExpandableHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * [ExpandableItemHelper] 相关的配置
 *
 * @author 985892345
 * 2023/4/19 11:36
 */
interface IExpandableItemConfig : ILongPressItemConfig {
  
  override fun isLongPress(
    event: IPointerEvent,
    page: ICoursePage,
    item: ITouchItem,
    child: View
  ): Boolean {
    // 如果存在 translation，则说明 View 不在本来的位置，就不允许激活长按
    if (child.translationX == 0F || child.translationY == 0F) {
      val t = item.lp.constraintTop
      val b = item.lp.constraintBottom
      val y = event.y.toInt()
      if ((y !in (t + Boundary)..(b - Boundary))) {
        return true
      }
    }
    return false
  }
  
  /**
   * 得到处理 View 扩展的工具类
   */
  fun getExpandableHandler(): IExpandableItemHandler {
    return DefaultExpandableHandler()
  }
  
  companion object Default : IExpandableItemConfig {
    const val Boundary = 44 // 距离边界的距离值，如果在这个值以内，则应该触发 Expand
  }
}