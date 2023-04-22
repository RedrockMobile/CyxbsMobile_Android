package com.mredrock.cyxbs.lib.course.item.touch.helper.longpress

import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import kotlin.math.abs

/**
 * 处理单个长按手指的 [ITouchItemHelper] 类
 *
 * - 只观察一个 [ILongPressItemConfig.isLongPress] 返回 true 的手指
 *
 * @author 985892345
 * 2023/4/19 13:02
 */
abstract class AbstractLongPressItemHelper(
  private val config: ILongPressItemConfig
) : ITouchItemHelper {
  
  /**
   * 设置监听，使用 protected 以支持子类重新设计
   */
  protected fun setLongPressItemListener(l: ILongPressItemListener) {
    mLongPressItemListener = l
  }
  
  private var mLongPressItemListener: ILongPressItemListener? = null
  
  protected var mPointerId = MotionEvent.INVALID_POINTER_ID
    private set
  protected var mInitialX = 0
    private set
  protected var mInitialY = 0
    private set
  protected var mLastMoveX = 0
    private set
  protected var mLastMoveY = 0
    private set
  
  // 当前长按状态。true -> 长按激活；false -> 长按还未激活；null -> 长按激活前因移动距离过大被取消
  protected var mIsInLongPress: Boolean? = null
    private set
  
  protected var mCoursePage: ICoursePage? = null
    private set
  protected var mTouchItem: ITouchItem? = null
    private set
  protected var mItemView: View? = null
    private set
  
  // 在长按激活时需要提前拦截子 View 的事件，所以该方法返回 true
  override fun isAdvanceIntercept(): Boolean {
    return mIsInLongPress == true
  }
  
  @CallSuper
  override fun onPointerTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem,
    page: ICoursePage
  ) {
    when (val action = event.action) {
      IPointerEvent.Action.DOWN -> {
        if (mPointerId == MotionEvent.INVALID_POINTER_ID) {
          if (config.isLongPress(event, page, item, child)) {
            mPointerId = event.pointerId
            mInitialX = event.x.toInt() // 重置
            mInitialY = event.y.toInt() // 重置
            mLastMoveX = mInitialX // 重置
            mLastMoveY = mInitialY // 重置
            mIsInLongPress = false // 重置
            mCoursePage = page
            mTouchItem = item
            mItemView = child
            startLongPress()
            mLongPressItemListener?.onDown(page, item, child, event)
          }
        }
      }
      IPointerEvent.Action.MOVE -> {
        if (event.pointerId == mPointerId) { // 只处理长按手指
          val isInLongPress = mIsInLongPress
          if (isInLongPress != null) {
            val x = event.x.toInt()
            val y = event.y.toInt()
            if (!isInLongPress) {
              // 长按激活前
              val touchSlop = ViewConfiguration.get(parent.context).scaledTouchSlop
              val isInTouchSlop =
                abs(x - mLastMoveX) <= touchSlop && abs(y - mLastMoveY) <= touchSlop
              // child 可能存在 margin，所以不用 child.x
              val l = item.lp.constraintLeft + child.translationX
              val r = item.lp.constraintRight + child.translationX
              val t = item.lp.constraintTop + child.translationY
              val b = item.lp.constraintBottom + child.translationY
              val isInChild = x.toFloat() in l..r && y.toFloat() in t..b
              if (!isInTouchSlop || !isInChild) {
                // 移动距离过大，需要取消长按
                cancelLongPress()
                mIsInLongPress = null // 结束
                mLongPressItemListener?.onLongPressCancel(
                  page,
                  item,
                  child,
                  event
                )
              }
            } else {
              // 该分支表明已经触发长按
              mLongPressItemListener?.onMove(
                page,
                item,
                child,
                x,
                y
              )
            }
            mLastMoveX = x
            mLastMoveY = y
          }
        }
      }
      IPointerEvent.Action.UP, IPointerEvent.Action.CANCEL -> {
        if (event.pointerId == mPointerId) { // 只处理长按手指
          if (mIsInLongPress == false) {
            // 没有激活长按就抬起手或者被 CANCEL
            cancelLongPress()
          }
          // 回调监听
          val isCancel = action == IPointerEvent.Action.CANCEL
          mLongPressItemListener?.onEventEnd(
            page,
            item,
            child,
            event,
            mIsInLongPress,
            isCancel
          )
          // 还原变量
          mPointerId = MotionEvent.INVALID_POINTER_ID
          mIsInLongPress = null
          mCoursePage = null
          mTouchItem = null
          mItemView = null
        }
      }
    }
  }
  
  // 长按激活时的回调
  private fun onLongPressed() {
    val page = mCoursePage ?: return
    val item = mTouchItem ?: return
    val view = mItemView ?: return
    mIsInLongPress = true
    mLongPressItemListener?.onLongPressed(
      page,
      item,
      view,
      mLastMoveX,
      mLastMoveY
    )
  }
  
  protected fun startLongPress() {
    mItemView?.postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout().toLong())
  }
  
  protected fun cancelLongPress() {
    mItemView?.removeCallbacks(mLongPressRunnable)
  }
  
  private val mLongPressRunnable = Runnable { onLongPressed() }
}