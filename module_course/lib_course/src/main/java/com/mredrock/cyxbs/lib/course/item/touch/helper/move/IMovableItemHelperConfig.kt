package com.mredrock.cyxbs.lib.course.item.touch.helper.move

import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import kotlin.math.hypot
import kotlin.math.pow

/**
 * [MovableItemHelper] 的配置类
 *
 * @author 985892345
 * 2023/2/19 20:52
 */
interface IMovableItemHelperConfig {
  
  /**
   * 新位置是否允许与 [view] 重叠
   *
   * 如果允许的话，则可以认为该 item 可以与 View 重叠显示，正常情况是不重叠显示的
   */
  fun isAllowOverlap(page: ICoursePage, view: View): Boolean = false
  
  /**
   * 是否允许长按移动
   * @param child [item] 对应的 View
   */
  fun isMovable(
    event: IPointerEvent,
    page: ICoursePage,
    item: ITouchItem,
    child: View
  ): Boolean = true
  
  /**
   * 是否允许移动到 [newLocation]
   */
  fun isMovableToNewLocation(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    newLocation: LocationUtil.Location
  ): Boolean = true
  
  /**
   * 得到最后移动到新位置或者回到原位置的动画时长
   */
  fun getMoveAnimatorDuration(dx: Float, dy: Float): Long {
    // 自己拟合的一条由距离求出时间的函数，感觉比较适合动画效果 :)
    // y = 50 * x^0.25 + 90
    return (hypot(dx, dy).pow(0.25F) * 50 + 90).toLong()
  }
  
  companion object Default : IMovableItemHelperConfig
}