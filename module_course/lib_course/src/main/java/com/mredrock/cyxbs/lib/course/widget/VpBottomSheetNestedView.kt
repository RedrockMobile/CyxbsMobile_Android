package com.mredrock.cyxbs.lib.course.widget

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
 * ### 一、中转嵌套滑动
 * 使用当前布局包裹在 Vp2 外面，这样 BottomSheet 就会直接与该布局进行嵌套关联
 *
 * 当 Vp2 里面的 NSV 需要寻找嵌套滑动父 View 时，就会找到当前 View，然后把事件分发给当前 View，当前 View 又把事件分发给 BottomSheet
 *
 * ### 二、取消 Vp2 中 rv 的嵌套滑动功能
 * ```
 * mViewPager.getChildAt(0).isNestedScrollingEnabled = false
 * ```
 * 因为 BottomSheet 找到嵌套子 View 的判断是查看该子 View 是否开启了嵌套滑动，所以可以直接取消 rv 的嵌套滑动功能，
 * 让 BottomSheet 找到 rv下面的 NSV
 *
 * **缺点:** 嵌套滑动失效，在你移动时会被 BottomSheet 直接拦截事件
 *
 *
 * ## bug 记录
 * ### 一、长按课表 item 后，下一次触摸时上下滑动不再与 BottomSheet 关联
 *
 * 原因：
 * 0. 触摸事件分为两次，分别为前一次和后一次，两次是分开独立的事件，即前一次经过了 UP 事件
 *
 * 1. 长按课表 item 时，此时为前一次事件，DOWN 事件经过了 RV(VP2中的) 和 NSV 的 onInterceptTouchEvent()，
 *   其中 RV 和 NSV 依次调用了 startNestedScroll()，最后 NSV 跟 VpBottomSheetNestedView 嵌套成功，
 *   RV 没有找到嵌套滑动父布局 (所以前一次事件中 RV 全程不会触发嵌套滑动，后面将不再考虑前一次事件中的 RV)
 *
 * 2. 前一次事件因为触发长按，调用了 parent.requestDisallowInterceptTouchEvent(true)
 *   导致 NSV 的 onInterceptTouchEvent() 不再回调
 *
 * 3. 前一次事件在收到 UP 事件时，因为 NSV 的 onInterceptTouchEvent() 不再被调用，
 *   所以 NSV 无法调用 stopNestedScroll() 结束嵌套
 *
 * 4. 后一次事件正常上下滑动，先是 DOWN，与前一次事件中的 DOWN 一样的步骤，但是 NSV 因为没有停止上一次的嵌套滑动，
 *   导致 NSV 这次调用 startNestedScroll() 不会通知 VpBottomSheetNestedView
 *
 * 5. 但是 RV 却向上遍历到 CoordinatorLayout，CoordinatorLayout 的 onStartNestedScroll() 被回调，
 *   但是却没有 Behavior 同意嵌套，最后 CoordinatorLayout 调用了 lp.setNestedScrollAccepted(type, false)，
 *   把 BottomSheetBehavior 对应 View 中的 lp.mDidAcceptNestedScrollTouch 设置成了 false，
 *   导致 BottomSheetBehavior 被取消了嵌套滑动 (CoordinatorLayout onNestedPreScroll() 会判断这个变量以决定是否分发嵌套滑动)
 *
 * 6. 所以就导致 bug 的出现
 *
 * 解决办法：
 * - 从 5 入手，取消 VP2 中 RV 的嵌套滑动 (但是如果有其他嵌套的 View，此问题会再次出现)
 * - 从 3 入手，在合适时间调用 NSV 的 stopNestedScroll() (原因：因为 NSV 在 RV 后面调用 startNestedScroll()，在 RV 设置成 false 后会被 NSV 再次改为 true)
 *
 *
 * ### 二、...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/13 14:15
 */
class VpBottomSheetNestedView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), NestedScrollingChild3, NestedScrollingParent3 {
  
  private val mNestedChildHelper = NestedScrollingChildHelper(this)
  private val mNestedParentHelper = NestedScrollingParentHelper(this)
  
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
    offsetInWindow: IntArray?,
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
    offsetInWindow: IntArray?,
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
    type: Int,
  ): Boolean {
    return mNestedChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type)
  }
  
  override fun dispatchNestedPreScroll(
    dx: Int,
    dy: Int,
    consumed: IntArray?,
    offsetInWindow: IntArray?,
    type: Int,
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
    consumed: IntArray,
  ) {
    mNestedChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed)
  }
  
  private var mTargetView: View? = null
  
  // NestedScrollingParent
  
  override fun onStartNestedScroll(
    child: View, target: View, axes: Int,
  ): Boolean {
    return onStartNestedScroll(child, target, axes, ViewCompat.TYPE_TOUCH)
  }
  
  override fun onNestedScrollAccepted(
    child: View, target: View, axes: Int,
  ) {
    mNestedParentHelper.onNestedScrollAccepted(child, target, axes)
  }
  
  override fun onStopNestedScroll(target: View) {
    onStopNestedScroll(target, ViewCompat.TYPE_TOUCH)
  }
  
  override fun onNestedScroll(
    target: View, dxConsumed: Int, dyConsumed: Int,
    dxUnconsumed: Int, dyUnconsumed: Int,
  ) {
    dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null)
  }
  
  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
    dispatchNestedPreScroll(dx, dy, consumed, null)
  }
  
  override fun onNestedFling(
    target: View, velocityX: Float, velocityY: Float, consumed: Boolean,
  ): Boolean {
    return dispatchNestedFling(velocityX, velocityY, consumed)
  }
  
  override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
    return dispatchNestedPreFling(velocityX, velocityY)
  }
  
  override fun getNestedScrollAxes(): Int {
    return mNestedParentHelper.nestedScrollAxes
  }
  
  // NestedScrollingParent2
  
  override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
    if (axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0) {
      if (startNestedScroll(axes, type)) {
        mTargetView = target
        return true
      }
    }
    return false
  }
  
  override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
    mNestedParentHelper.onNestedScrollAccepted(child, target, axes, type)
  }
  
  override fun onStopNestedScroll(target: View, type: Int) {
    stopNestedScroll(type)
    mTargetView = null
    mNestedParentHelper.onStopNestedScroll(target, type)
  }
  
  override fun onNestedScroll(
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int,
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
    consumed: IntArray,
  ) {
    dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null, type, consumed)
  }
  
  /**
   * 嵌套滑动中判断 View 是否能继续滑动的关键方法
   * https://blog.csdn.net/yuzhangzhen/article/details/109018619
   */
  override fun canScrollVertically(direction: Int): Boolean {
    return mTargetView?.canScrollVertically(direction) ?: super.canScrollVertically(direction)
  }
  
  override fun canScrollHorizontally(direction: Int): Boolean {
    return mTargetView?.canScrollHorizontally(direction) ?: super.canScrollHorizontally(direction)
  }
}