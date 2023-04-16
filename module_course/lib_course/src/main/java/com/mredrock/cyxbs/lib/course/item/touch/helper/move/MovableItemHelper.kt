package com.mredrock.cyxbs.lib.course.item.touch.helper.move

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseScrollControl
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.course.utils.forEachReversed
import com.mredrock.cyxbs.lib.utils.utils.VibratorUtil
import com.ndhzs.netlayout.draw.ItemDecoration
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import kotlin.math.abs
import kotlin.math.min

/**
 * 长按 item 实现移动的帮助类
 *
 * @author 985892345
 * 2023/2/6 21:34
 */
class MovableItemHelper(
  val config: IMovableItemHelperConfig = IMovableItemHelperConfig.Default
) : ITouchItemHelper {
  
  /**
   * 添加移动监听
   */
  fun addMovableListener(l: IMovableListener) {
    mMovableListener.add(l)
  }
  
  fun removeMovableListener(l: IMovableListener) {
    mMovableListener.remove(l)
  }
  
  private val mMovableListener = arrayListOf<IMovableListener>()
  
  private var mPointerId = MotionEvent.INVALID_POINTER_ID
  private var mInitialX = 0
  private var mInitialY = 0
  private var mLastMoveX = 0
  private var mLastMoveY = 0
  
  // 当前长按状态。true -> 长按激活；false -> 长按还未激活；null -> 长按激活前因移动距离过大被取消
  private var mIsInLongPress: Boolean? = null
  
  private var mCoursePage: ICoursePage? = null
  private var mTouchItem: ITouchItem? = null
  private var mItemView: View? = null
  
  private var mIsLockedNoon = false // 是否锁定了中午时间段
  private var mIsLockedDusk = false // 是否锁定了中午时间段
  
  private var mOldTranslationX = 0F
  private var mOldTranslationY = 0F
  private var mOldTranslationZ = 0F
  
  private var mViewDiffY = 0 // view.top - course.getAbsoluteY(mPointerId) 的值
  
  private var mInitialScrollY = 0 // 长按激活时的课表滚轴 scrollY
  
  // Scroll 滚动不一定会导致 mItemDecoration 被回调，所以需要强制刷新
  private val mScrollYChangedListener =
    ICourseScrollControl.OnScrollYChangedListener { _, _ ->
      if (mIsInLongPress == true) {
        mCoursePage?.course?.invalidate()
      }
    }
  
  // 用于在每一帧时回调 move() 方法
  private val mItemDecoration = object : ItemDecoration {
    
    private val refreshRunnable = Runnable { move() }
    
    override fun onDrawBelow(canvas: Canvas, view: View) {
      if (mIsInLongPress == true) {
        view.post(refreshRunnable) // 使用 post 防止绘制卡顿
      }
    }
  }
  
  // 在长按激活时需要提前拦截子 View 的事件，所以该方法返回 true
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
          if (config.isMovable(event, page, item, child)) {
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
            mMovableListener.forEachReversed {
              it.onDown(page, item, child, mInitialX, mInitialY)
            }
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
              // 长按激活前
              val touchSlop = ViewConfiguration.get(parent.context).scaledTouchSlop
              val isInTouchSlop = abs(x - mLastMoveX) <= touchSlop && abs(y - mLastMoveY) <= touchSlop
              val isInChild = (x - child.x.toInt()) in 0..child.width &&
                (y - child.y.toInt()) in 0..child.height
              if (!isInTouchSlop || !isInChild) {
                // 移动距离过大，需要取消长按
                mLongPressRunnable.cancel(parent)
                mIsInLongPress = null // 结束
                mMovableListener.forEachReversed {
                  it.onLongPressCancel(page, item, child, mInitialX, mInitialY, x, y)
                }
              }
              mLastMoveX = x
              mLastMoveY = y
            } else {
              // 该分支表明已经触发长按
              mLastMoveX = x
              mLastMoveY = y
              page.course.invalidate()
            }
          }
        }
      }
      IPointerEvent.Action.UP, IPointerEvent.Action.CANCEL -> {
        if (event.pointerId == mPointerId) {
          val course = page.course
          // 长按状态判断
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
              course.removeItemDecoration(mItemDecoration)
              
              var newLocation: LocationUtil.Location? = null // 需要移动到的新位置
              if (action == IPointerEvent.Action.UP) {
                val location = LocationUtil.getLocation(child, course) {
                  config.isAllowOverlap(page, child)
                }
                if (location != null) {
                  if (config.isMovableToNewLocation(page, item, child, location)) {
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
          // 回调监听
          if (action == IPointerEvent.Action.UP) {
            mMovableListener.forEachReversed {
              it.onUp(page, item, child, mInitialX, mInitialY, mLastMoveX, mLastMoveY, mIsInLongPress)
            }
          } else {
            mMovableListener.forEachReversed {
              it.onCancel(page, item, child, mInitialX, mInitialY, mLastMoveX, mLastMoveY, mIsInLongPress)
            }
          }
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
  private fun longPressStart() {
    val page = mCoursePage ?: return
    val item = mTouchItem ?: return
    val view = mItemView ?: return
    val course = page.course
    
    mIsInLongPress = true
    VibratorUtil.start(36) // 长按被触发来个震动提醒
    course.getParent().requestDisallowInterceptTouchEvent(true) // 禁止父布局拦截
    unfoldNoonDuskIfNeed()
    
    course.addOnScrollYChanged(mScrollYChangedListener)
    course.addItemDecoration(mItemDecoration)
    
    mOldTranslationX = view.translationX
    mOldTranslationY = view.translationY
    mOldTranslationZ = view.translationZ
    
    view.translationZ = mOldTranslationZ + mPointerId * 0.1F + 2.1F // 让 View 显示在其他 View 上方
    mViewDiffY = view.top - course.getAbsoluteY(mPointerId)
    mInitialScrollY = course.getScrollCourseY()
  
    mMovableListener.forEachReversed {
      it.onLongPressStart(page, item, view, mInitialX, mInitialY, mLastMoveX, mLastMoveY)
    }
  }
  
  // 展开中午或者傍晚，如果需要的话
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
        // 其实只需要在移动期间禁止中午发生改变就可以了，不然会导致 item 大小跟随改变
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
  
  /**
   * item 移动的核心代码
   *
   * ### 不要直接调用该方法
   * 请使用 page.course.invalidate() 来间接调用该方法 (invalidate() -> mItemDecoration -> move())。
   * 原因在于：
   * - 减少计算次数
   * - 如果手动调用，会导致调用 [changeScrollYIfNeed] 后出现鬼畜的移动效果
   */
  private fun move() {
    if (mIsInLongPress != true) return
    val item = mTouchItem ?: return
    val view = mItemView ?: return
    val page = mCoursePage ?: return
    val course = page.course
    
    changeScrollYIfNeed()
    unfoldNoonDuskIfNeed()
  
    // 因为存在 scrollY 的改变和中午傍晚的展开导致的坐标系变化的问题
    // 所以要使用 ScrollView 的绝对位置来计算 Y 轴偏移量
    val absoluteY = course.getAbsoluteY(mPointerId)
    val scrollY = course.getScrollCourseY()
    view.translationX = (mLastMoveX - mInitialX).toFloat() + mOldTranslationX
    view.translationY = (absoluteY + mViewDiffY - view.top) + mOldTranslationY + (scrollY - mInitialScrollY)
  
    mMovableListener.forEachReversed {
      it.onMove(page, item, view, mInitialX, mInitialY, mLastMoveX, absoluteY + absoluteY + scrollY)
    }
  }
  
  // 触摸事件结束时调用
  private fun over(
    location: LocationUtil.Location?,
    page: ICoursePage,
    item: ITouchItem,
    child: View
  ) {
    if (location != null) {
      // 移动到新位置
      item.lp.changeLocation(location) // 修改 item 的 lp
      child.layoutParams = item.lp // 先把 child 给移过去
      val oldX = child.x
      val oldY = child.y
      val oldTransitionZ = child.translationZ
      child.doOnNextLayout {
        // 在 child 重新布局后开启动画
        mMovableListener.forEachReversed { listener ->
          listener.onOverAnimStart(location, page, item, child)
        }
        val dx = oldX - (it.left + mOldTranslationX)
        val dy = oldY - (it.top + mOldTranslationY)
        MoveAnimation(dx, dy, config.getMoveAnimatorDuration(dx, dy)) { x, y, fraction ->
          child.translationX = mOldTranslationX + x
          child.translationY = mOldTranslationY + y
          child.translationZ =
            (oldTransitionZ - mOldTranslationZ) * (1 - fraction) + mOldTranslationZ
          mMovableListener.forEachReversed { listener ->
            listener.onOverAnimUpdate(location, page, item, child, fraction)
          }
        }.doOnEnd {
          child.translationX = mOldTranslationX
          child.translationY = mOldTranslationY
          child.translationZ = mOldTranslationZ
          mMovableListener.forEachReversed { listener ->
            listener.onOverAnimEnd(location, page, item, child)
          }
        }.start()
      }
    } else {
      // 回到原位置
      mMovableListener.forEachReversed { listener ->
        listener.onOverAnimStart(null, page, item, child)
      }
      val dx = child.x - (child.left + mOldTranslationX)
      val dy = child.y - (child.top + mOldTranslationY)
      val oldTransitionZ = child.translationZ
      MoveAnimation(dx, dy, config.getMoveAnimatorDuration(dx, dy)) { x, y, fraction ->
        child.translationX = mOldTranslationX + x
        child.translationY = mOldTranslationY + y
        child.translationZ = (oldTransitionZ - mOldTranslationZ) * (1 - fraction) + mOldTranslationZ
        mMovableListener.forEachReversed { listener ->
          listener.onOverAnimUpdate(null, page, item, child, fraction)
        }
      }.doOnEnd {
        child.translationX = mOldTranslationX
        child.translationY = mOldTranslationY
        child.translationZ = mOldTranslationZ
        mMovableListener.forEachReversed { listener ->
          listener.onOverAnimEnd(null, page, item, child)
        }
      }.start()
    }
  }
  
  // 控制课表滚轴滚动
  private fun changeScrollYIfNeed() {
    val page = mCoursePage ?: return
    val child = mItemView ?: return
    val course = page.course
    
    val courseScrollY = course.getScrollCourseY()
    val courseScrollHeight = course.getScrollHeight()
    val topHeight = child.y.toInt() - courseScrollY
    val bottomHeight = topHeight + child.height
    val moveBoundary = 60 // 移动的边界值
    
    // 向上滚动，即手指移到底部，需要显示下面的内容
    val isNeedScrollUp =
      bottomHeight > courseScrollHeight - moveBoundary
        && bottomHeight - (courseScrollHeight - moveBoundary) > (moveBoundary - topHeight)
        && course.canCourseScrollVertically(1) // 是否滑到底
  
    // 向下滚动，即手指移到顶部，需要显示上面的内容
    val isNeedScrollDown =
      topHeight < moveBoundary
        && (moveBoundary - topHeight) > bottomHeight - (courseScrollHeight - moveBoundary)
        && course.canCourseScrollVertically(-1) // 是否滑到顶
    
    val velocity = if (isNeedScrollUp) {
      // 速度最小为 6，最大为 12，与边界的差值成线性关系
      min((bottomHeight - (courseScrollHeight - moveBoundary)) / 10 + 6, 12)
    } else if (isNeedScrollDown) {
      -min(((moveBoundary - topHeight) / 10 + 6), 12)
    } else 0
    course.scrollCourseBy(velocity) // 这里调用后会回调 mScrollYChangedListener，然后又回调 move()
  }
}