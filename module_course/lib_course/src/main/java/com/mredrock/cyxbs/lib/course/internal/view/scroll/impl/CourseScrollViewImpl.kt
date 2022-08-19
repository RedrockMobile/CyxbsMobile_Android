package com.mredrock.cyxbs.lib.course.internal.view.scroll.impl

import android.content.Context
import android.util.AttributeSet
import com.mredrock.cyxbs.lib.course.internal.view.scroll.AbstractCourseScrollView

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 0:39
 */
class CourseScrollViewImpl @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet?,
  defStyleAttr: Int = 0
) : AbstractCourseScrollView(context, attrs, defStyleAttr) {
  
  override fun scrollCourseBy(dy: Int) {
    scrollBy(0, dy)
  }
  
  override fun scrollCourseY(y: Int) {
    scrollY = y
  }
}