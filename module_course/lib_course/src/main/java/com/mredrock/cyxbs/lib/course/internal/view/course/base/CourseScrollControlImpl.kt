package com.mredrock.cyxbs.lib.course.internal.view.course.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseScrollControl

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
      if (parent is ICourseScrollControl) break
      parent = parent.parent
    }
    if (parent !is ICourseScrollControl) error("在父 View 中找不到 ${ICourseScrollControl::class.simpleName} 的实现类")
    return parent
  }
  
  final override fun scrollCourseBy(dy: Int) {
    getScrollImpl().scrollCourseBy(dy)
  }
  
  final override fun scrollCourseY(y: Int) {
    getScrollImpl().scrollCourseY(y)
  }
  
  final override fun getScrollCourseY(): Int {
    return getScrollImpl().getScrollCourseY()
  }
  
  final override fun getAbsoluteY(pointerId: Int): Int {
    return getScrollImpl().getAbsoluteY(pointerId)
  }
  
  final override fun getScrollHeight(): Int {
    return getScrollImpl().getScrollHeight()
  }
  
  final override fun canCourseScrollVertically(direction: Int): Boolean {
    return getScrollImpl().canCourseScrollVertically(direction)
  }
  
  final override fun measureChildWithRatio(
    child: View,
    parentWidthMeasureSpec: Int,
    parentHeightMeasureSpec: Int,
    childWidthRatio: Float,
    childHeightRatio: Float
  ) {
    val lp = child.layoutParams.net()
    val parentWidth = MeasureSpec.getSize(parentWidthMeasureSpec) - paddingLeft - paddingRight
    val wMode = MeasureSpec.getMode(parentWidthMeasureSpec)
    val childWidth = (childWidthRatio * parentWidth).toInt()
    val childWidthMeasureSpec = getChildMeasureSpec(
      MeasureSpec.makeMeasureSpec(childWidth, wMode),
      lp.leftMargin + lp.rightMargin, lp.width
    )
    
    val parentHeight = MeasureSpec.getSize(parentHeightMeasureSpec) - paddingTop - paddingBottom
    val childHeight = (childHeightRatio * parentHeight).toInt()
    val childHeightMeasureSpec = getChildMeasureSpec(
      MeasureSpec.makeMeasureSpec(
        childHeight,
        /*
        * 这里为什么直接给 EXACTLY ?
        * 1、目前需求（22年）课表在开始时不显示中午和傍晚时间段，我设计的 NetLayout 可以把高度设置成
        *    wrap_content，再调用 setRowInitialWeight() 使中午和傍晚时间段初始的比重为 0，
        *    从而实现不展开中午和傍晚时刚好铺满外控件大小，即外面的 ScrollView 刚好不能滚动
        * 2、课表如果要显示中午和傍晚时间段，则外布局需要包裹一个 NestedScrollView，这时，父布局得到的
        *    测量模式为 UNSPECIFIED，该模式会使课表初始状态不再填充父布局，所以需要把子 View 的测量改为 EXACTLY 模式
        * 3、改子 View 的测量模式原因在于 TextView 等一般的 View 在收到 UNSPECIFIED 模式时会使用自身合适的高度值，
        *    而不是 childHeight 这个值，就会导致课表初始状态不再填充父布局
        * */
        MeasureSpec.EXACTLY
      ),
      lp.topMargin + lp.bottomMargin, lp.height
    )
    
    child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
  }
}