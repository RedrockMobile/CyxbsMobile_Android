package com.mredrock.cyxbs.lib.course.item.touch.helper.move

import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.core.animation.addListener
import androidx.core.view.doOnNextLayout
import com.mredrock.cyxbs.lib.course.BuildConfig
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseScrollControl
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.utils.VibratorUtil
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import kotlin.math.abs

/**
 * .
 *
 * @author 985892345
 * 2023/2/6 21:34
 */
class MovableItemHelper(
  val config: IMovableItemHelperConfig = IMovableItemHelperConfig.Default
) : ITouchItemHelper {
  
  companion object {
    
    private var isShowDebugToast = false
    
    fun debugToast() {
      if (!isShowDebugToast && BuildConfig.DEBUG) {
        isShowDebugToast = true
        toast("实验性功能，只在 debug 状态下开启")
      }
    }
  }
  
  private var mPointerId = MotionEvent.INVALID_POINTER_ID
  private var mInitialX = 0
  private var mInitialY = 0
  private var mLastMoveX = 0
  private var mLastMoveY = 0
  
  private var mIsInLongPress: Boolean? = null
  
  private var mCoursePage: ICoursePage? = null
  private var mTouchItem: ITouchItem? = null
  private var mItemView: View? = null
  
  private var mIsLockedNoon = false
  private var mIsLockedDusk = false
  
  private var mOldTranslationX = 0F
  private var mOldTranslationY = 0F
  private var mOldTranslationZ = 0F
  
  private var mViewDiffY = 0 // view.top - course.getAbsoluteY(mPointerId) 的值
  
  private var mOldScrollY = 0
  
  // Scroll 滚动会导致整个坐标系的移动，也需要同步修改 View 的位置
  private val mScrollYChangedListener =
    ICourseScrollControl.OnScrollYChangedListener { oldScrollY, scrollY ->
      move()
    }
  
  // 在展开中午时，中午高度以下的 View 都会向下移动，此时也需要同步修改 View 的位置
  private val mLayoutChangeListener =
    View.OnLayoutChangeListener { _, _, top, _, _, _, oldTop, _, _ ->
      if (top != oldTop) {
        move()
      }
    }
  
  override fun isAdvanceIntercept(): Boolean {
    return mIsInLongPress == true
  }
  
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
          if (config.isMovable(page.course, item)) {
            mPointerId = event.pointerId
            mInitialX = event.x.toInt() // 重置
            mInitialY = event.y.toInt() // 重置
            mLastMoveX = mInitialX // 重置
            mLastMoveY = mInitialY // 重置
            mIsInLongPress = false // 重置
            mLongPressRunnable.start(parent)
            mIsLockedNoon = false // 重置
            mIsLockedDusk = false // 重置
            mCoursePage = page
            mTouchItem = item
            mItemView = child
          }
        }
      }
      IPointerEvent.Action.MOVE -> {
        if (event.pointerId == mPointerId) {
          val isInLongPress = mIsInLongPress
          if (isInLongPress != null) {
            val x = event.x.toInt()
            val y = event.y.toInt()
            if (!isInLongPress) {
              val touchSlop = ViewConfiguration.get(parent.context).scaledTouchSlop
              if (abs(x - mLastMoveX) > touchSlop || abs(y - mLastMoveY) > touchSlop) {
                mLongPressRunnable.cancel(parent)
                mIsInLongPress = null // 结束
              }
            } else {
              // 该分支表明已经触发长按
              move()
            }
            mLastMoveX = x
            mLastMoveY = y
          }
        }
      }
      IPointerEvent.Action.UP, IPointerEvent.Action.CANCEL -> {
        if (event.pointerId == mPointerId) {
          val course = page.course
          when (mIsInLongPress) {
            false -> {
              // 没有激活长按就抬起手或者被 CANCEL
              mLongPressRunnable.cancel(parent)
            }
            true -> {
              // 激活了长按后抬手或者被 CANCEL
              if (mIsLockedNoon) {
                page.unlockFoldNoon()
              }
              if (mIsLockedDusk) {
                page.unlockFoldDusk()
              }
              course.removeOnScrollYChanged(mScrollYChangedListener)
              child.removeOnLayoutChangeListener(mLayoutChangeListener)
              
              var newLocation: LocationUtil.Location? = null // 需要移动到的新位置
              if (action == IPointerEvent.Action.UP) {
                val location = LocationUtil.getLocation(child, course) {
                  config.isAllowOverlap(course, child)
                }
                if (location != null) {
                  if (config.isMovableToNewLocation(course, item, location)) {
                    newLocation = location
                  }
                }
              }
              over(newLocation, page, item, child) // 最后执行动画
            }
            null -> {
              // 回调这里说明前面长按因为距离超过 touchSlop 被取消
            }
          }
          mPointerId = MotionEvent.INVALID_POINTER_ID
          mIsInLongPress = null
          mCoursePage = null
          mTouchItem = null
          mItemView = null
        }
      }
    }
  }
  
  private fun longPressStart() {
    debugToast()
    val page = mCoursePage ?: return
    val view = mItemView ?: return
    val course = page.course
    
    mIsInLongPress = true
    VibratorUtil.start(36) // 长按被触发来个震动提醒
    course.getParent().requestDisallowInterceptTouchEvent(true) // 禁止父布局拦截
    unfoldNoonDuskIfNeed()
    
    course.addOnScrollYChanged(mScrollYChangedListener)
    view.addOnLayoutChangeListener(mLayoutChangeListener)
    
    mOldTranslationX = view.translationX
    mOldTranslationY = view.translationY
    mOldTranslationZ = view.translationZ
    
    view.translationZ = mOldTranslationZ + mPointerId * 0.1F + 1.1F // 让 View 显示在其他 View 上方
    mViewDiffY = view.top - course.getAbsoluteY(mPointerId)
    mOldScrollY = course.getScrollCourseY()
  }
  
  private fun unfoldNoonDuskIfNeed() {
    val page = mCoursePage ?: return
    val view = mItemView ?: return
    if (!mIsLockedNoon) {
      val viewY = view.y.toInt()
      val isViewContainNoon =
        page.compareNoonPeriodByHeight(viewY) * page.compareNoonPeriodByHeight(viewY + view.height) <= 0
      if (isViewContainNoon) {
        // 这里需要展开中午
        page.unfoldNoon()
        page.lockFoldNoon() // 锁定中午，后面会还原
        mIsLockedNoon = true
        // 虽然不一定会展开成功，因为可能会在其他地方被锁住，但是解锁次数需要等于上锁次数才能完全解锁，所以最终还是仍被锁的
        // 其实只要在移动期间禁止中午发生改变就可以了，不然会导致 item 大小跟随改变
      }
    }
    if (!mIsLockedDusk) {
      val viewY = view.y.toInt()
      val isViewContainDusk =
        page.compareDuskPeriodByHeight(viewY) * page.compareDuskPeriodByHeight(viewY + view.height) <= 0
      if (isViewContainDusk) {
        // 这里需要展开傍晚
        page.unfoldDusk()
        page.lockFoldDusk() // 锁定中午，后面会还原
        mIsLockedDusk = true
      }
    }
  }
  
  private val mLongPressRunnable = object : Runnable {
    override fun run() {
      longPressStart()
    }
    
    fun start(view: View) {
      view.postDelayed(this, ViewConfiguration.getLongPressTimeout().toLong())
    }
    
    fun cancel(view: View) {
      view.removeCallbacks(this)
    }
  }
  
  private fun move() {
    val view = mItemView ?: return
    val page = mCoursePage ?: return
    val course = page.course
    view.translationX = (mLastMoveX - mInitialX).toFloat() + mOldTranslationX
    view.translationY =
      (course.getAbsoluteY(mPointerId) + mViewDiffY - view.top) + mOldTranslationY +
        (course.getScrollCourseY() - mOldScrollY)
    unfoldNoonDuskIfNeed()
  }
  
  private fun over(
    location: LocationUtil.Location?,
    page: ICoursePage,
    item: ITouchItem,
    child: View
  ) {
    if (location != null) {
      // 移动到新位置
      item.lp.changeLocation(location)
      child.layoutParams = item.lp // 先把 child 给移过去
      val oldX = child.x
      val oldY = child.y
      val oldTransitionZ = child.translationZ
      child.doOnNextLayout {
        // 在 child 重新布局后开启动画
        val dx = oldX - (it.left + mOldTranslationX)
        val dy = oldY - (it.top + mOldTranslationY)
        MoveAnimation(dx, dy, config.getMoveAnimatorDuration(dx, dy)) { x, y, fraction ->
          child.translationX = mOldTranslationX + x
          child.translationY = mOldTranslationY + y
          child.translationZ =
            (oldTransitionZ - mOldTranslationZ) * (1 - fraction) + mOldTranslationZ
        }.doOnEnd {
          child.translationX = mOldTranslationX
          child.translationY = mOldTranslationY
          child.translationZ = mOldTranslationZ
        }.start()
      }
    } else {
      // 回到原位置
      val dx = child.x - (child.left + mOldTranslationX)
      val dy = child.y - (child.top + mOldTranslationY)
      val oldTransitionZ = child.translationZ
      MoveAnimation(dx, dy, config.getMoveAnimatorDuration(dx, dy)) { x, y, fraction ->
        child.translationX = mOldTranslationX + x
        child.translationY = mOldTranslationY + y
        child.translationZ = (oldTransitionZ - mOldTranslationZ) * (1 - fraction) + mOldTranslationZ
      }.doOnEnd {
        child.translationX = mOldTranslationX
        child.translationY = mOldTranslationY
        child.translationZ = mOldTranslationZ
      }.start()
    }
  }
  
  // 移动动画的封装
  private class MoveAnimation(
    private val dx: Float,
    private val dy: Float,
    private val time: Long,
    private val onChange: (x: Float, y: Float, fraction: Float) -> Unit
  ) {
    private val animator = ValueAnimator.ofFloat(0F, 1F)
    fun start(): MoveAnimation {
      animator.run {
        addUpdateListener {
          val fraction = it.animatedFraction
          val x = dx * (1 - fraction)
          val y = dy * (1 - fraction)
          onChange.invoke(x, y, fraction)
        }
        duration = time
        interpolator = OvershootInterpolator(0.6F) // 个人认为 0.6F 的回弹比较合适
        start()
      }
      return this
    }
    
    fun doOnEnd(onEnd: () -> Unit): MoveAnimation {
      animator.addListener(onEnd = { onEnd.invoke() })
      return this
    }
  }
}