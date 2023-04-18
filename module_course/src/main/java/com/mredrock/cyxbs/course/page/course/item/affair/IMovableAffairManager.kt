package com.mredrock.cyxbs.course.page.course.item.affair

import android.view.View
import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.LocationUtil

/**
 * 可长按移动事务的管理类
 *
 * @author 985892345
 * 2023/2/23 12:53
 */
interface IMovableAffairManager {
  fun isMovableToNewLocation(
    page: ICoursePage,
    affair: AffairItem,
    child: View,
    newLocation: LocationUtil.Location,
    data: AffairData
  ): Boolean
  
  fun onLongPressStart(
    page: ICoursePage,
    affair: AffairItem,
    child: View,
    data: AffairData
  ) {}
  
  fun onOverAnimStart(
    newLocation: LocationUtil.Location?,
    page: ICoursePage,
    affair: AffairItem,
    child: View,
    data: AffairData
  ) {}
  
  fun onOverAnimEnd(
    newLocation: LocationUtil.Location?,
    page: ICoursePage,
    affair: AffairItem,
    child: View,
    data: AffairData
  ) {}
}