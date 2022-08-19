package com.mredrock.cyxbs.lib.course.internal.item

import android.view.View
import com.mredrock.cyxbs.lib.course.internal.affair.IAffairItem
import com.mredrock.cyxbs.lib.course.internal.lesson.ILessonItem
import com.mredrock.cyxbs.lib.course.internal.view.course.lp.CourseLayoutParam

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 13:07
 */
interface IItem : IItemData {
  val view: View
  val lp: CourseLayoutParam
  
  val isLessonItem: Boolean
    get() = this is ILessonItem
  
  val isAffairItem: Boolean
    get() = this is IAffairItem
}