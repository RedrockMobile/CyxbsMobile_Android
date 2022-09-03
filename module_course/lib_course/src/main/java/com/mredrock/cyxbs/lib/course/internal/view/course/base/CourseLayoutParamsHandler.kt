package com.mredrock.cyxbs.lib.course.internal.view.course.base

import android.content.Context
import android.util.AttributeSet
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.internal.view.course.lp.ItemLayoutParams
import com.mredrock.cyxbs.lib.course.internal.view.course.lp.XmlLayoutParams
import com.ndhzs.netlayout.attrs.NetLayoutParams

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 15:35
 */
abstract class CourseLayoutParamsHandler @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = R.attr.courseLayoutStyle,
  defStyleRes: Int = 0
) : CourseContainerImpl(context, attrs, defStyleAttr, defStyleRes) {
  
  override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
    return XmlLayoutParams(context, attrs)
  }
  
  override fun generateLayoutParams(lp: LayoutParams): LayoutParams {
    error("不允许使用除 ${ItemLayoutParams::class.simpleName} 以外的 LayoutParams")
  }
  
  override fun generateDefaultLayoutParams(): LayoutParams {
    error("不允许直接 addView(View)")
  }
  
  override fun checkLayoutParams(p: LayoutParams?): Boolean {
    return p is ItemLayoutParams
  }
  
  private var mCompareLayoutParams: Comparator<ItemLayoutParams>? = null
  
  final override fun setCompareLayoutParams(compare: Comparator<ItemLayoutParams>) {
    mCompareLayoutParams = compare
  }
  
  final override fun compareLayoutParams(o1: NetLayoutParams, o2: NetLayoutParams): Int {
    return mCompareLayoutParams?.compare(o1 as ItemLayoutParams, o2 as ItemLayoutParams)
      ?: super.compareLayoutParams(o1, o2)
  }
}