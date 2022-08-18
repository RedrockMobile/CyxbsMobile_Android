package com.mredrock.cyxbs.lib.course.item

import android.view.View
import com.mredrock.cyxbs.lib.course.view.course.lp.CourseLayoutParam

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 13:07
 */
interface IItem : IItemData {
  val view: View
  val lp: CourseLayoutParam
}