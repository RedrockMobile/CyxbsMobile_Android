package com.mredrock.cyxbs.lib.course.internal.lesson

import com.mredrock.cyxbs.lib.course.internal.period.am.IAmPeriod
import com.mredrock.cyxbs.lib.course.internal.period.night.INightPeriod
import com.mredrock.cyxbs.lib.course.internal.period.pm.IPmPeriod
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseContainer

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 15:28
 */
interface ILessonContainer : IAmPeriod, IPmPeriod, INightPeriod {
  
  fun addLessonItem(lesson: ILessonItem)
  
  fun removeLessonItem(lesson: ILessonItem)
  
  /**
   * 得到某节课开始前的高度值
   */
  fun getLessonStartHeight(num: Int): Int
  
  /**
   * 得到某节课结束时的高度值
   */
  fun getLessonEndHeight(num: Int): Int
  
  /**
   * 添加 [ILessonItem] 和移除 [ILessonItem] 的监听
   *
   * ## 注意
   * 这个是只有调用 [addLessonItem] 方法后才会有回调，如果调用的是 [ICourseContainer.addItem] 是不会收到回调的
   */
  fun addLessonExistListener(l: OnLessonExistListener)
  
  /**
   * 寻找对应坐标的 [ILessonItem]
   */
  fun findLessonItemUnderByXY(x: Int, y: Int): ILessonItem?
  
  interface OnLessonExistListener {
    fun onLessonAddedBefore(lesson: ILessonItem) {}
    fun onLessonAddedAfter(lesson: ILessonItem) {}
    fun onLessonRemovedBefore(lesson: ILessonItem) {}
    fun onLessonRemovedAfter(lesson: ILessonItem) {}
  }
}