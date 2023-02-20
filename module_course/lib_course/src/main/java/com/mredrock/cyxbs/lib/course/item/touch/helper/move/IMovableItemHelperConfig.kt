package com.mredrock.cyxbs.lib.course.item.touch.helper.move

import android.view.View
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import kotlin.math.hypot
import kotlin.math.pow

/**
 * .
 *
 * @author 985892345
 * 2023/2/19 20:52
 */
interface IMovableItemHelperConfig {
  
  /**
   * 新位置是否允许与 [child] 重叠
   */
  fun isAllowOverlap(parent: ICourseViewGroup, child: View): Boolean
  
  /**
   * 是否允许长按移动
   * @param child [item] 对应的 View
   */
  fun isMovable(event: IPointerEvent, parent: ICourseViewGroup, item: ITouchItem, child: View): Boolean
  
  /**
   * 是否允许移动到 [newLocation]
   */
  fun isMovableToNewLocation(
    parent: ICourseViewGroup,
    item: ITouchItem,
    newLocation: LocationUtil.Location
  ): Boolean
  
  /**
   * 得到最后移动到新位置或者回到原位置的动画时长
   */
  fun getMoveAnimatorDuration(dx: Float, dy: Float): Long
  
  companion object Default : IMovableItemHelperConfig {
    
    override fun isAllowOverlap(parent: ICourseViewGroup, child: View): Boolean {
      return false
    }
  
    override fun isMovable(
      event: IPointerEvent,
      parent: ICourseViewGroup,
      item: ITouchItem,
      child: View
    ): Boolean {
      return true
    }
  
    override fun isMovableToNewLocation(
      parent: ICourseViewGroup,
      item: ITouchItem,
      newLocation: LocationUtil.Location
    ): Boolean {
      return true
    }
  
    override fun getMoveAnimatorDuration(dx: Float, dy: Float): Long {
      // 自己拟合的一条由距离求出时间的函数，感觉比较适合动画效果 :)
      // y = 50 * x^0.25 + 90
      return (hypot(dx, dy).pow(0.25F) * 50 + 90).toLong()
    }
  }
}