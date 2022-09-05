package com.mredrock.cyxbs.lib.course.internal.lesson

import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 15:28
 */
interface ILessonContainer {
  
  /**
   * 添加时候成功，因为可能会被拦截
   */
  fun addLessonItem(lesson: ILessonItem): Boolean
  
  fun removeLessonItem(lesson: ILessonItem)
  
  /**
   * 添加 [ILessonItem] 和移除 [ILessonItem] 的监听
   *
   * ## 注意
   * 这个是只有调用 [addLessonItem] 方法后才会有回调，如果调用的是 [IItemContainer.addItem] 是不会收到回调的
   */
  fun addLessonExistListener(l: OnLessonExistListener)
  
  /**
   * 寻找对应坐标的 [ILessonItem]
   */
  fun findLessonItemUnderByXY(x: Int, y: Int): ILessonItem?
  
  interface OnLessonExistListener {
    fun isAllowToAddLesson(lesson: ILessonItem): Boolean = false
    fun onLessonAddedBefore(lesson: ILessonItem) {}
    fun onLessonAddedAfter(lesson: ILessonItem) {}
    fun onLessonRemovedBefore(lesson: ILessonItem) {}
    fun onLessonRemovedAfter(lesson: ILessonItem) {}
  }
}