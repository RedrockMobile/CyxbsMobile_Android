package com.mredrock.cyxbs.lib.course.view.course.base

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.view.course.ICourseScrollControl

/**
 * 负责处理课表滚轴的父类
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 14:48
 */
abstract class CourseScrollControlImpl @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = R.attr.courseLayoutStyle,
  defStyleRes: Int = 0
) : CourseMultiTouchImpl(context, attrs, defStyleAttr, defStyleRes), ICourseScrollControl {
  
  private fun getScrollImpl(): ICourseScrollControl {
    var parent = parent
    while (parent is ViewGroup) {
      if (parent is ICourseScrollControl) {
        break
      }
      parent = parent.parent
    }
    if (parent !is ICourseScrollControl) error("在父 View 中找不到 ${ICourseScrollControl::class.simpleName} 的实现类")
    return parent
  }
  
  override fun scrollCourseBy(dy: Int) {
    getScrollImpl().scrollCourseBy(dy)
  }
  
  override fun scrollCourseY(y: Int) {
    getScrollImpl().scrollCourseY(y)
  }
}