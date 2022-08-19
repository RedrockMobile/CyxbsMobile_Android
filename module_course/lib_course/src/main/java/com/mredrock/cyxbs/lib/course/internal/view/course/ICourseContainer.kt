package com.mredrock.cyxbs.lib.course.internal.view.course

import com.mredrock.cyxbs.lib.course.internal.affair.IAffairContainer
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.lesson.ILessonContainer
import com.mredrock.cyxbs.lib.course.internal.period.am.IAmPeriod
import com.mredrock.cyxbs.lib.course.internal.period.dusk.IDuskPeriod
import com.mredrock.cyxbs.lib.course.internal.period.night.INightPeriod
import com.mredrock.cyxbs.lib.course.internal.period.noon.INoonPeriod
import com.mredrock.cyxbs.lib.course.internal.period.pm.IPmPeriod

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 14:29
 */
interface ICourseContainer : ILessonContainer,
  IAffairContainer,
  IAmPeriod, INoonPeriod, IPmPeriod, IDuskPeriod, INightPeriod
{
  /**
   * 寻找对应坐标的 [IItem]
   */
  fun findItemUnderByXY(x: Int, y: Int): IItem?
  
  fun addItem(item: IItem)
  
  fun removeItem(item: IItem)
  
  /**
   * 添加 [IItem] 和移除 [IItem] 的监听
   */
  fun addItemExistListener(l: OnItemExistListener)
  
  interface OnItemExistListener {
    fun onItemAddedBefore(item: IItem) {}
    fun onItemAddedAfter(item: IItem) {}
    fun onItemRemovedBefore(item: IItem) {}
    fun onItemRemovedAfter(item: IItem) {}
  }
}