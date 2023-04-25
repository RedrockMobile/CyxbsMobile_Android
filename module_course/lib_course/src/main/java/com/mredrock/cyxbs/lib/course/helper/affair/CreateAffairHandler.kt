package com.mredrock.cyxbs.lib.course.helper.affair

import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.helper.affair.expose.IBoundary
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchAffairItem
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchCallback
import com.mredrock.cyxbs.lib.course.helper.base.ILongPressTouchHandler
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseScrollControl
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.utils.utils.VibratorUtil
import com.ndhzs.netlayout.draw.ItemDecoration
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent.Action.*
import kotlin.math.max
import kotlin.math.min

/**
 * 整个核心机制是：
 * onPointerTouchEvent() -> mScrollRunnable -> invalidate() -> mItemDecoration -> refreshTouchAffairView()
 *
 * @author 985892345
 * @date 2022/9/20 17:11
 */
class CreateAffairHandler(
  val course: ICourseViewGroup,
  val iTouch: ITouchCallback,
  val iBoundary: IBoundary,
  val touchAffairItem: ITouchAffairItem
) : ILongPressTouchHandler {
  
  private var mInitialRow = 0 // Down 事件中触摸的初始行数
  private var mInitialColumn = 0 // Down 事件中触摸的初始列数
  
  private var mTouchRow = 0 // 当前触摸的行数
  private var mShowRow = 0 // 实际显示的行数
  private var mUpperRow = 0 // 选择区域的上限
  private var mLowerRow = course.rowCount - 1 // 选择区域的下限
  
  private var mPointerId = 0
  
  private var mIsMoving = false // 控制 refreshTouchAffairView 的调用
  
  // Scroll 滚动不一定会导致 mItemDecoration 被回调，所以需要强制刷新
  private val mScrollYChangedListener =
    ICourseScrollControl.OnScrollYChangedListener { _, _ ->
      if (mIsInLongPress) {
        mIsMoving = true
        course.invalidate() // 触发刷新，然后 mItemDecoration 会回调 refreshTouchAffairView
      }
    }
  
  // 用于在每一帧时回调 refreshTouchAffairView() 方法
  private val mItemDecoration = object : ItemDecoration {
    
    override fun onDrawBelow(canvas: Canvas, view: View) {
      if (mIsMoving) {
        refreshTouchAffairView() // 调用 refreshTouchAffairView 方法
        mIsMoving = false
      }
    }
  }
  
  private var mIsInLongPress = false
  
  private var mTopMargin = 0
  private var mBottomMargin = 0
  
  override fun onPointerTouchEvent(event: IPointerEvent, view: ViewGroup) {
    val x = event.x.toInt()
    val y = event.y.toInt()
    when (event.action) {
      DOWN -> {
        mInitialRow = course.getRow(y) // 重置
        mInitialColumn = course.getColumn(x) // 重置
        mPointerId = event.pointerId // 重置
        mTouchRow = mInitialRow // 重置
        mShowRow = mInitialRow // 重置
        mUpperRow = 0 // 重置
        mLowerRow = course.rowCount - 1 // 重置
        mIsInLongPress = false // 重置
        mTopMargin = touchAffairItem.lp.topMargin // 重置
        mBottomMargin = touchAffairItem.lp.bottomMargin // 重置
      }
      MOVE -> {
        mScrollRunnable.startIfCan() // 触发 mScrollRunnable 中的核心代码
        mIsMoving = true
        course.invalidate() // 触发刷新，然后 mItemDecoration 会回调 refreshTouchAffairView
      }
      UP, CANCEL -> {
        if (mIsInLongPress) {
          mScrollRunnable.cancel()
          course.removeOnScrollYChanged(mScrollYChangedListener)
          course.removeItemDecoration(mItemDecoration)
          touchAffairItem.onMoveEnd(course)
          iTouch.onTouchEnd(
            mPointerId,
            mInitialRow,
            mTouchRow,
            mShowRow,
            event.action == CANCEL
          )
        }
      }
    }
  }
  
  override fun onLongPressed() {
    mIsInLongPress = true
    // 禁止父布局拦截
    course.getParent().requestDisallowInterceptTouchEvent(true)
    course.addOnScrollYChanged(mScrollYChangedListener)
    course.addItemDecoration(mItemDecoration)
    
    VibratorUtil.start(36) // 长按被触发来个震动提醒
    iTouch.onLongPressed(mPointerId, mInitialRow, mInitialColumn)
    
    touchAffairItem.onMoveStart(course, mInitialRow, mInitialColumn)
    
    iTouch.onShowTouchAffairItem(course, touchAffairItem)
  }
  
  /**
   * 刷新 [ITouchAffairItem] 的核心方法
   *
   * ### 不要直接调用该方法
   * 请使用 page.course.invalidate() 和设置 mIsMoving=true 来间接调用该方法
   * (invalidate() -> mItemDecoration -> refreshTouchAffairView())。
   *
   * 原因在于：
   * - 一帧只能回调一次 refreshTouchAffairView，多次回调会多次计算导致效果出现问题 (比如滚轴会加倍移动)
   */
  private fun refreshTouchAffairView() {
    if (!mIsMoving) return
    val y = course.getAbsoluteY(mPointerId) + course.getScrollCourseY()
    val nowTouchRow = course.getRow(y) // 当前触摸的行数
    if (nowTouchRow < mTouchRow) {
      // 手指往上移动
      if (nowTouchRow < mInitialRow) {
        // 在 item 上方移动
        mUpperRow = max(iBoundary.getUpperRow(course, mInitialRow, nowTouchRow, mInitialColumn), 0)
        mShowRow = max(mUpperRow, nowTouchRow)
      } else {
        mShowRow = nowTouchRow
      }
    } else if (nowTouchRow > mTouchRow) {
      // 手指往下移动
      if (nowTouchRow > mInitialRow) {
        // 在 item 下方移动
        mLowerRow = min(
          iBoundary.getLowerRow(course, mInitialRow, nowTouchRow, mInitialColumn),
          course.rowCount - 1
        )
        mShowRow = min(mLowerRow, nowTouchRow)
      } else {
        mShowRow = nowTouchRow
      }
    }
    if (nowTouchRow != mTouchRow) {
      mTouchRow = nowTouchRow
      iTouch.onTouchMove(mPointerId, mInitialRow, mTouchRow, mShowRow)
    }
    if (mTouchRow < mUpperRow) {
      val newY = course.getRowsHeight(0, mUpperRow - 1) + mTopMargin
      touchAffairItem.onSingleMove(course, mInitialRow, newY)
    } else if (mTouchRow > mLowerRow) {
      val newY = course.getRowsHeight(0, mLowerRow) - mBottomMargin
      touchAffairItem.onSingleMove(course, mInitialRow, newY)
    } else {
      touchAffairItem.onSingleMove(course, mInitialRow, y)
    }
  }
  
  // 滑到显示区域顶部或者底部时，使 mCourseScrollView 滚动的 Runnable
  private val mScrollRunnable = object : Runnable {
    
    var isInScrolling = false // 是否处于滚动状态
      private set
    
    private var velocity = 0 // 滚动的速度
    
    override fun run() {
      if (isAllowScrollAndCalculateVelocity()) {
        // 核心运行代码
        course.scrollCourseBy(velocity) // 滚动
        mIsMoving = true
        course.invalidate() // 刷新，之后会回调 mItemDecoration
        course.postOnAnimation(this) // 一直循环下去
      } else {
        isInScrolling = false
        velocity = 0
      }
    }
    
    /**
     * 如果能开启滚动的话就开启滚动，注意：滚动是会自己取消的
     */
    fun startIfCan() {
      if (!isInScrolling) { // 防止重复允许 Runnable
        isInScrolling = true
        run()
      }
    }
    
    /**
     * 取消滚动
     */
    fun cancel() {
      if (isInScrolling) {
        isInScrolling = false
        course.removeCallbacks(this)
      }
    }
    
    /**
     * 是否允许滚动，如果允许，则计算滚动速度给 [velocity] 变量
     */
    private fun isAllowScrollAndCalculateVelocity(): Boolean {
      val absoluteY = course.getAbsoluteY(mPointerId)
      val moveBoundary = 100 // 移动的边界值
      val minV = 6 // 最小速度
      val maxV = 20 // 最大速度
      // 速度最小为 6，最大为 20，与边界的差值成线性关系
      
      // 向上滚动，即手指移到底部，需要显示下面的内容
      val isNeedScrollUp =
        absoluteY > course.getScrollOuterHeight() - moveBoundary
          && mTouchRow <= mLowerRow // 当前触摸的行数在下限以上
          && course.canCourseScrollVertically(1) // 没有滑到底
      
      // 向下滚动，即手指移到顶部，需要显示上面的内容
      val isNeedScrollDown =
        absoluteY < moveBoundary
          && mTouchRow >= mUpperRow // 当前触摸的行数在上限以下
          && course.canCourseScrollVertically(-1) // 没有滑到顶
      val isAllowScroll = isNeedScrollUp || isNeedScrollDown
      if (isAllowScroll) {
        velocity = if (isNeedScrollUp) {
          min((absoluteY - (course.getScrollOuterHeight() - moveBoundary)) / 2 + minV, maxV)
        } else {
          // 如果是向下滚动稍稍降低加速度，因为顶部手指可以移动出去，很容易满速
          -min(((moveBoundary - absoluteY) / 10 + minV), maxV)
        }
      }
      return isAllowScroll
    }
  }
}