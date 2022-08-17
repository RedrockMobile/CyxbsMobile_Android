package com.mredrock.cyxbs.lib.course.view

import com.mredrock.cyxbs.lib.course.affair.IAffairContainer
import com.mredrock.cyxbs.lib.course.item.IItem
import com.mredrock.cyxbs.lib.course.lesson.ILessonContainer

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 14:29
 */
interface ICourseContainer : ILessonContainer, IAffairContainer {
  
  /**
   * 添加 item 前的监听
   */
  fun addItemAddedListener(l: OnItemAddedListener)
  
  interface OnItemAddedListener {
    fun addItemBefore(item: IItem)
  }
}