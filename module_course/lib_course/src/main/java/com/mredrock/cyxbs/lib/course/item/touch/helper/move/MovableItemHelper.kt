package com.mredrock.cyxbs.lib.course.item.touch.helper.move

import android.graphics.Canvas
import android.view.View
import androidx.core.view.doOnNextLayout
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseScrollControl
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.helper.longpress.AbstractLongPressItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.helper.longpress.ILongPressItemListener
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.utils.LocationUtil
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.utils.MoveAnimation
import com.mredrock.cyxbs.lib.course.utils.forEachReversed
import com.mredrock.cyxbs.lib.utils.utils.VibratorUtil
import com.ndhzs.netlayout.draw.ItemDecoration
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import kotlin.math.min

/**
 * 长按 item 实现移动的帮助类
 *
 * ## 注意
 * - 如果你需要重写默认的移动逻辑，请传入你自己的 [config]
 *
 * 因为只需要处理一个长按的手指，所以继承于 [AbstractLongPressItemHelper]
 *
 * @author 985892345
 * 2023/2/6 21:34
 */
class MovableItemHelper(
  val config: IMovableItemConfig = IMovableItemConfig.Default
) : AbstractLongPressItemHelper(config) {
  
  /**
   * 添加移动监听
   */
  fun addMovableListener(l: IMovableItemListener) {
    mMovableItemListeners.add(l)
  }
  
  fun removeMovableListener(l: IMovableItemListener) {
    mMovableItemListeners.remove(l)
  }
  
  // MovableHandler 不能放进 mMovableItemListeners 中，因为 mMovableItemListeners 是倒序遍历的
  // MovableHandler 需要保证第一个回调
  private val mMovableHandler = config.getMovableHandler()
  private val mMovableItemListeners = arrayListOf<IMovableItemListener>()
  private val mLongPressMovableItemListener = LongPressMovableItemListener()
  
  init {
    setLongPressItemListener(mLongPressMovableItemListener)
  }
  
  private inner class LongPressMovableItemListener : ILongPressItemListener {
    
    private var mIsLockedNoon = false // 是否锁定了中午时间段
    private var mIsLockedDusk = false // 是否锁定了中午时间段
  
    private var mIsMoving = false // 控制 move 的调用
    
    override fun onDown(page: ICoursePage, item: ITouchItem, child: View, event: IPointerEvent) {
      mIsLockedNoon = false // 重置
      mIsLockedDusk = false // 重置
      mIsMoving = false // 重置
      mMovableHandler.onDown(page, item, child, event)
      mMovableItemListeners.forEachReversed {
        it.onDown(page, item, child, event)
      }
    }
    
    override fun onLongPressed(
      page: ICoursePage,
      item: ITouchItem,
      child: View,
      x: Int,
      y: Int,
      pointerId: Int
    ) {
      val course = page.course
      
      VibratorUtil.start(36) // 长按被触发来个震动提醒
      course.getParent().requestDisallowInterceptTouchEvent(true) // 禁止父布局拦截
      unfoldNoonDuskIfNeed()
      
      course.addOnScrollYChanged(mScrollYChangedListener)
      course.addOnLayoutChangeListener(mOnLayoutChanged)
      course.addItemDecoration(mItemDecoration)
      
      mMovableHandler.onLongPressed(page, item, child, x, y, pointerId)
      mMovableItemListeners.forEachReversed {
        it.onLongPressed(page, item, child, x, y, pointerId)
      }
    }
    
    override fun onLongPressCancel(
      page: ICoursePage,
      item: ITouchItem,
      child: View,
      event: IPointerEvent
    ) {
      mMovableHandler.onLongPressCancel(page, item, child, event)
      mMovableItemListeners.forEachReversed {
        it.onLongPressCancel(page, item, child, event)
      }
    }
    
    override fun onMove(page: ICoursePage, item: ITouchItem, child: View, x: Int, y: Int) {
      tryPostMove()
    }
    
    override fun onEventEnd(
      page: ICoursePage,
      item: ITouchItem,
      child: View,
      event: IPointerEvent,
      isInLongPress: Boolean?,
      isCancel: Boolean
    ) {
      val course = page.course
      if (isInLongPress == true) {
        // 激活了长按后抬手或者被 CANCEL
        if (mIsLockedNoon) {
          page.unlockFoldNoon()
        }
        if (mIsLockedDusk) {
          page.unlockFoldDusk()
        }
        course.removeOnScrollYChanged(mScrollYChangedListener)
        course.removeOnLayoutChangeListener(mOnLayoutChanged)
        course.removeItemDecoration(mItemDecoration)
        
        var newLocation: LocationUtil.Location? = null // 需要移动到的新位置
        if (!isCancel) {
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
      mMovableHandler.onEventEnd(page, item, child, event, isInLongPress, isCancel)
      mMovableItemListeners.forEachReversed {
        it.onEventEnd(page, item, child, event, isInLongPress, isCancel)
      }
    }
    
    // 触摸事件结束时调用
    private fun over(
      location: LocationUtil.Location?,
      page: ICoursePage,
      item: ITouchItem,
      child: View
    ) {
      fun startAnim(location: LocationUtil.Location?) {
        mMovableHandler.onOverAnimStart(location, page, item, child)
        mMovableItemListeners.forEachReversed { listener ->
          listener.onOverAnimStart(location, page, item, child)
        }
        MoveAnimation(mMovableHandler.getMoveAnimatorDuration()) { fraction ->
          mMovableHandler.onOverAnimUpdate(location, page, item, child, fraction)
          mMovableItemListeners.forEachReversed { listener ->
            listener.onOverAnimUpdate(location, page, item, child, fraction)
          }
        }.doOnEnd {
          mMovableHandler.onOverAnimEnd(location, page, item, child)
          mMovableItemListeners.forEachReversed { listener ->
            listener.onOverAnimEnd(location, page, item, child)
          }
        }.start()
      }
      if (location != null) {
        // 移动到新位置
        item.lp.changeLocation(location) // 修改 item 的 lp
        child.layoutParams = item.lp // 先把 child 给移过去
        child.doOnNextLayout {
          // 在 child 重新布局后开启动画，因为上面修改了 lp，导致并不会进行布局，此时 item 还在原位置
          startAnim(location)
        }
      } else {
        // 回到原位置
        startAnim(null)
      }
    }
  
    ///////////////////////////////
    //
    //          移动逻辑
    //
    ///////////////////////////////
  
    /**
     * 尝试发送一次刷新来回调 [mItemDecoration]
     *
     * 但值得注意的是，并不是直接调用 move()，而是在每一帧时回调 move() 方法，
     * 官方源码中经常这样操作，目的是为了统一回调时机，减少回调次数
     *
     * 这里调用后会在下一帧中回调 [mItemDecoration]，然后触发刷新操作
     */
    private fun tryPostMove() {
      if (!mIsMoving && mIsInLongPress == true) {
        mIsMoving = true
        mCoursePage?.course?.invalidate()
      }
    }
  
    // Scroll 滚动不一定会导致 mItemDecoration 被回调，所以需要强制刷新
    private val mScrollYChangedListener =
      ICourseScrollControl.OnScrollYChangedListener { _, _ ->
        tryPostMove()
      }
  
    // 课表布局发生改变时也需要回调 mItemDecoration，然后触发 move()
    private val mOnLayoutChanged =
      View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
        tryPostMove()
      }
  
    // 用于在每一帧时回调 move() 方法
    private val mItemDecoration = object : ItemDecoration {
      override fun onDrawBelow(canvas: Canvas, view: View) {
        if (mIsMoving && mIsInLongPress == true) {
          move() // 调用 move 方法
          mIsMoving = false
        }
      }
  
      /**
       * item 移动的核心代码
       *
       * ### 不要直接调用该方法
       * 请使用 page.course.invalidate() 和设置 mIsMoving=true 来间接调用该方法 (invalidate() -> mItemDecoration -> move())。
       * 原因在于：
       * - 一帧只能回调一次 move，多次回调会多次计算导致效果出现问题 (比如滚轴会加倍移动)，
       *  官方源码中经常这样操作，目的是为了统一回调时机，减少回调次数
       * - 如果手动调用，会导致调用 [changeScrollYIfNeed] 后出现鬼畜的移动效果
       */
      private fun move() {
        if (mIsInLongPress != true) return
        if (!mIsMoving) return
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
    
        val x = mLastMoveX
        val y = absoluteY + scrollY
        mMovableHandler.onMove(page, item, view, x, y)
        mMovableItemListeners.forEachReversed {
          it.onMove(page, item, view, x, y)
        }
      }
    }
  
  
    ///////////////////////////////
    //
    //           滚轴相关
    //
    ///////////////////////////////
    
    // 控制课表滚轴滚动
    private fun changeScrollYIfNeed() {
      val page = mCoursePage ?: return
      val child = mItemView ?: return
      val course = page.course
      
      val courseScrollY = course.getScrollCourseY()
      val scrollOuterHeight = course.getScrollOuterHeight()
      val topHeight = child.y.toInt() - courseScrollY // 相对于滚轴外高度的值
      val bottomHeight = topHeight + child.height // 相对于滚轴外高度的值
      val moveBoundary = 60 // 移动的边界值
      
      // 向上滚动，即手指移到底部，需要显示下面的内容
      val isNeedScrollUp =
        bottomHeight > scrollOuterHeight - moveBoundary
          // 底部差值需要大于顶部差值
          && bottomHeight - (scrollOuterHeight - moveBoundary) > (moveBoundary - topHeight)
          && course.canCourseScrollVertically(1) // 是否滑到底
      
      // 向下滚动，即手指移到顶部，需要显示上面的内容
      val isNeedScrollDown =
        topHeight < moveBoundary
          // 顶部差值需要大于底部差值
          && (moveBoundary - topHeight) > bottomHeight - (scrollOuterHeight - moveBoundary)
          && course.canCourseScrollVertically(-1) // 是否滑到顶
      
      val velocity = if (isNeedScrollUp) {
        // 速度最小为 6，最大为 12，与边界的差值成线性关系
        min((bottomHeight - (scrollOuterHeight - moveBoundary)) / 10 + 6, 12)
      } else if (isNeedScrollDown) {
        -min(((moveBoundary - topHeight) / 10 + 6), 12)
      } else 0
      course.scrollCourseBy(velocity) // 这里调用后会回调 mScrollYChangedListener，然后又回调 move()
    }
  
  
    ///////////////////////////////
    //
    //         中午傍晚相关
    //
    ///////////////////////////////
  
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
  }
}