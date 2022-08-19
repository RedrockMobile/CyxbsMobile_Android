package com.mredrock.cyxbs.lib.course.internal.view.course.base

import android.content.Context
import android.util.AttributeSet
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.internal.view.course.lp.CourseLayoutParam
import com.mredrock.cyxbs.lib.course.internal.view.course.lp.XmlLayoutParam

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 15:35
 */
abstract class CourseLayoutParamHandler @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = R.attr.courseLayoutStyle,
  defStyleRes: Int = 0
) : CourseFoldImpl(context, attrs, defStyleAttr, defStyleRes) {
  
  override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
    return XmlLayoutParam(context, attrs)
  }
  
  override fun generateLayoutParams(lp: LayoutParams): LayoutParams {
    error("不允许使用除 ${CourseLayoutParam::class.simpleName} 以外的 LayoutParams")
  }
  
  override fun generateDefaultLayoutParams(): LayoutParams {
    error("不允许直接 addView(View)")
  }
  
  override fun checkLayoutParams(p: LayoutParams?): Boolean {
    return p is CourseLayoutParam
  }
}