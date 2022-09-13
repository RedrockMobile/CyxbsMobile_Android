package com.mredrock.cyxbs.lib.course.fragment.bottomsheet

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.*

/**
 * 解决 BottomSheet 跟 vp 嵌套的 bug
 *
 * 因为 BottomSheet 只能跟第一个布局进行嵌套滑动，但是它没有判断嵌套的类型 (垂直还是水平)，
 * 所以直接把 Vp2 里面的 rv 拿来嵌套了
 *
 * ## 解决方案
 * ### 中转嵌套滑动
 * 使用当前布局包裹在 Vp2 外面，这样 BottomSheet 就会直接该布局进行嵌套关联
 *
 * 当 Vp2 里面的 NSV 需要寻找嵌套滑动父 View 时，就会找到当前 View，然后把事件分发给当前 View，当前 View 又把事件分发给 BottomSheet
 *
 * ### 取消 Vp2 中 rv 的嵌套滑动功能
 * ```
 * mViewPager.getChildAt(0).isNestedScrollingEnabled = false
 * ```
 * 因为 BottomSheet 找到嵌套子 View 的判断是查看该子 View 是否开启了嵌套滑动，所以可以直接取消 rv 的嵌套滑动功能，
 * 让 BottomSheet 找到 rv下面的 NSV
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/13 14:15
 */
class VpBottomSheetNestedView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), NestedScrollingChild3, NestedScrollingParent3 {
  
  private val mNestedChildHelper = NestedScrollingChildHelper(this)
  
  init {
    isNestedScrollingEnabled = true
  }
  
  // NestedScrollingChild
  
  override fun setNestedScrollingEnabled(enabled: Boolean) {
    mNestedChildHelper.isNestedScrollingEnabled = enabled
  }
  
  override fun isNestedScrollingEnabled(): Boolean {
    return mNestedChildHelper.isNestedScrollingEnabled
  }
  
  override fun startNestedScroll(axes: Int): Boolean {
    return mNestedChildHelper.startNestedScroll(axes)
  }
  
  override fun stopNestedScroll() {
    mNestedChildHelper.stopNestedScroll()
  }
  
  override fun hasNestedScrollingParent(): Boolean {
    return mNestedChildHelper.hasNestedScrollingParent()
  }
  
  override fun dispatchNestedScroll(
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    offsetInWindow: IntArray?
  ): Boolean {
    return mNestedChildHelper.dispatchNestedScroll(
      dxConsumed,
      dyConsumed,
      dxUnconsumed,
      dyUnconsumed,
      offsetInWindow
    )
  }
  
  override fun dispatchNestedPreScroll(
    dx: Int,
    dy: Int,
    consumed: IntArray?,
    offsetInWindow: IntArray?
  ): Boolean {
    return mNestedChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
  }
  
  override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
    return mNestedChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
  }
  
  override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
    return mNestedChildHelper.dispatchNestedPreFling(velocityX, velocityY)
  }
  
  // NestedScrollingChild2
  
  override fun startNestedScroll(axes: Int, type: Int): Boolean {
    return mNestedChildHelper.startNestedScroll(axes, type)
  }
  
  override fun stopNestedScroll(type: Int) {
    mNestedChildHelper.stopNestedScroll(type)
  }
  
  override fun hasNestedScrollingParent(type: Int): Boolean {
    return mNestedChildHelper.hasNestedScrollingParent(type)
  }
  
  override fun dispatchNestedScroll(
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    offsetInWindow: IntArray?,
    type: Int
  ): Boolean {
    return mNestedChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type)
  }
  
  override fun dispatchNestedPreScroll(
    dx: Int,
    dy: Int,
    consumed: IntArray?,
    offsetInWindow: IntArray?,
    type: Int
  ): Boolean {
    return mNestedChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
  }
  
  // NestedScrollingChild3
  
  override fun dispatchNestedScroll(
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    offsetInWindow: IntArray?,
    type: Int,
    consumed: IntArray
  ) {
    mNestedChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed)
  }
  
  
  
  // NestedScrollingParent
  
  private var mNestedScrollAxes = SCROLL_AXIS_NONE
  
  override fun onStartNestedScroll(
    child: View, target: View, axes: Int
  ): Boolean {
    return startNestedScroll(axes).also {
      if (it) mNestedScrollAxes = axes
    }
  }
  
  override fun onNestedScrollAccepted(
    child: View, target: View, axes: Int
  ) {
    // 该方法由 NestedScrollingChildHelper 内部在使用了 startNestedScroll() 后主动调用
    // 所以这里不需要做什么
  }
  
  override fun onStopNestedScroll(target: View) {
    stopNestedScroll()
    mNestedScrollAxes = SCROLL_AXIS_NONE
  }
  
  override fun onNestedScroll(
    target: View, dxConsumed: Int, dyConsumed: Int,
    dxUnconsumed: Int, dyUnconsumed: Int
  ) {
    dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null)
  }
  
  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
    dispatchNestedPreScroll(dx, dy, consumed, null)
  }
  
  override fun onNestedFling(
    target: View, velocityX: Float, velocityY: Float, consumed: Boolean
  ): Boolean {
    return dispatchNestedFling(velocityX, velocityY, consumed)
  }
  
  override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
    return dispatchNestedPreFling(velocityX, velocityY)
  }
  
  override fun getNestedScrollAxes(): Int {
    return mNestedScrollAxes
  }
  
  // NestedScrollingParent2
  
  override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
    return startNestedScroll(axes, type).also {
      if (it) mNestedScrollAxes = axes
    }
  }
  
  override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
    // 该方法由 NestedScrollingChildHelper 内部在使用了 startNestedScroll() 后主动调用
    // 所以这里不需要做什么
  }
  
  override fun onStopNestedScroll(target: View, type: Int) {
    stopNestedScroll(type)
    mNestedScrollAxes = SCROLL_AXIS_NONE
  }
  
  override fun onNestedScroll(
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int
  ) {
    dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null, type)
  }
  
  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
    dispatchNestedPreScroll(dx, dy, consumed, null, type)
  }
  
  // NestedScrollingParent3
  
  override fun onNestedScroll(
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int,
    consumed: IntArray
  ) {
    dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null, type, consumed)
  }
}