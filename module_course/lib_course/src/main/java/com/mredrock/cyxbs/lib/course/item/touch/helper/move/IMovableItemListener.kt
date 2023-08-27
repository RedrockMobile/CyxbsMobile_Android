package com.mredrock.cyxbs.lib.course.item.touch.helper.move

import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.helper.longpress.ILongPressItemListener
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.utils.LocationUtil

/**
 * [MovableItemHelper] 的回调
 *
 * @author 985892345
 * 2023/2/20 11:19
 */
interface IMovableItemListener : ILongPressItemListener {
  
  /**
   * 结束动画开始
   *
   * ## 注意
   * 如果 [newLocation] 不为 null，此时 item 已经移动到了新位置，即 item.lp 已经被修改为 [newLocation]
   *
   * @param newLocation 新的位置。如果为 null，则回到原位置
   */
  fun onOverAnimStart(
    newLocation: LocationUtil.Location?,
    page: ICoursePage,
    item: ITouchItem,
    child: View,
  ) {
  }
  
  /**
   * 结束动画执行中
   *
   * @param newLocation 新的位置。如果为 null，则回到原位置
   * @param fraction 动画进度，0 -> 1
   */
  fun onOverAnimUpdate(
    newLocation: LocationUtil.Location?,
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    fraction: Float
  ) {
  }
  
  /**
   * 结束动画结束
   *
   * @param newLocation 新的位置。如果为 null，则回到原位置
   */
  fun onOverAnimEnd(
    newLocation: LocationUtil.Location?,
    page: ICoursePage,
    item: ITouchItem,
    child: View,
  ) {
  }
}