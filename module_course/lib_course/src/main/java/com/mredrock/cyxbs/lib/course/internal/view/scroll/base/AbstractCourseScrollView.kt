package com.mredrock.cyxbs.lib.course.internal.view.scroll.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.iterator
import androidx.core.widget.NestedScrollView
import com.mredrock.cyxbs.lib.course.internal.view.scroll.ICourseScroll
import kotlin.math.max

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 0:37
 */
abstract class AbstractCourseScrollView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet?,
  defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr), ICourseScroll {
  
  init {
    /**
     * 取消 Android11 及以下的滑到边缘的虚影，Android12 及以上滑到边缘的拉伸效果
     *
     * Android12 后滑到边缘时拉伸效果，会导致嵌套滑动失效，所以需要取消该效果
     */
    overScrollMode = OVER_SCROLL_NEVER
  }
  
  override fun postDelayed(delayInMillis: Long, action: Runnable?) {
    postDelayed(action, delayInMillis)
  }
  
  /**
   * 重写该方法的几个原因：
   * 1、为了在 UNSPECIFIED 模式下，课表也能得到 NestedScrollView 的高度
   *
   * 2、NestedScrollView 与 ScrollView 在对于子 View 高度处理时在下面这个方法不一样, 导致
   *    NestedScrollView 中子 View 必须使用具体的高度, 设置成 wrap_content 或 match_parent
   *    都将无效，具体的可以去看 ScrollView 和 NestedScrollView 中对于这同一方法的源码
   *
   * 3、题外话：在 NestedScrollView 中嵌套 RecyclerView 会使 RecyclerView 的懒加载失效，直接原因就与
   *    这个方法有关，而使用 ScrollView 就不会造成懒加载失效的情况
   *
   * 4、至于为什么 NestedScrollView 与 ScrollView 在该方法不同，我猜测原因是为了兼容以前的 Android 版本，
   *    在 ViewGroup#getChildMeasureSpec() 方法中可以发现使用了一个静态变量 sUseZeroUnspecifiedMeasureSpec
   *    来判断 UNSPECIFIED 模式下子 View 该得到的大小，但可能设计 NestedScrollView “偷懒”了，没有加这个东西
   */
  override fun measureChildWithMargins(
    child: View,
    parentWidthMeasureSpec: Int,
    widthUsed: Int,
    parentHeightMeasureSpec: Int,
    heightUsed: Int
  ) {
    val lp = child.layoutParams as MarginLayoutParams
    
    val childWidthMeasureSpec = getChildMeasureSpec(
      parentWidthMeasureSpec,
      paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin
        + widthUsed, lp.width
    )
    val usedTotal = paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin + heightUsed
    val childHeightMeasureSpec: Int = MeasureSpec.makeMeasureSpec(
      max(0, MeasureSpec.getSize(parentHeightMeasureSpec) - usedTotal),
      MeasureSpec.UNSPECIFIED
    )
    
    child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
  }
  
  final override fun getIterable(): Iterable<View> {
    return object : Iterable<View> {
      override fun iterator(): Iterator<View> {
        return this@AbstractCourseScrollView.run { iterator() }
      }
    }
  }
}