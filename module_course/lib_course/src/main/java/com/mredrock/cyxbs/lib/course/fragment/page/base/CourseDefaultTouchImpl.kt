package com.mredrock.cyxbs.lib.course.fragment.page.base

import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import com.mredrock.cyxbs.lib.course.fragment.page.expose.ICourseDefaultTouch
import com.mredrock.cyxbs.lib.course.helper.show.CourseDownAnimDispatcher
import com.mredrock.cyxbs.lib.course.helper.ScrollTouchHandler
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import kotlin.math.abs

/**
 * 这里面写需要默认添加的触摸事件帮助类
 *
 * ## 注意
 * - 该类实现了 [ITouchItem] 的逻辑
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 18:23
 */
abstract class CourseDefaultTouchImpl : AbstractCoursePageFragment(), ICourseDefaultTouch {
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val dispatcher = getCourseDownAnimDispatcher()
    if (dispatcher != null) {
      course.addPointerDispatcher(dispatcher)
    }
    course.setDefaultPointerDispatcher(DefaultPointerDispatcher())
  }
  
  override fun getCourseDownAnimDispatcher(): CourseDownAnimDispatcher? {
    return CourseDownAnimDispatcher(course)
  }
  
  /**
   * 该类为事件分发优先级最低的默认分发者
   *
   * 为了不拦截子 View 的点击事件，采取了跟 ScrollView 一样的策略，在 MOVE 超过 touchSlop 距离后才进行拦截。
   * 同时，为了保证 [ITouchItem] 能够得到 DOWN 事件，所以手动调用了 onPointerTouchEvent() 方法
   */
  class DefaultPointerDispatcher : IPointerDispatcher {
  
    private val mDefaultPointerHandler = DefaultPointerHandler()
  
    private var mLastMoveX = 0F
    private var mLastMoveY = 0F
    
    override fun isPrepareToIntercept(event: IPointerEvent, view: ViewGroup): Boolean {
      when (event.action) {
        IPointerEvent.Action.DOWN -> {
          // 因为 getInterceptHandler() 在 DOWN 时会返回 null，所以需要手动调用来传递 DOWN 事件
          mDefaultPointerHandler.onPointerTouchEvent(event, view)
          return true
        }
        IPointerEvent.Action.UP, IPointerEvent.Action.CANCEL -> {
          // 如果这里能接受到回调，说明 getInterceptHandler() 没有 return handler
          // 因为没有 return，所以 mDefaultPointerHandler 不会收到 onPointerTouchEvent() 回调，需要手动调用
          mDefaultPointerHandler.onPointerTouchEvent(event, view)
        }
        IPointerEvent.Action.MOVE -> {
          // 因为在 DOWN 就 return true，所以 MOVE 是不会回调的
        }
      }
      return false
    }
  
    override fun getInterceptHandler(event: IPointerEvent, view: ViewGroup): IPointerTouchHandler? {
      val x = event.x
      val y = event.y
      when (event.action) {
        IPointerEvent.Action.DOWN -> {
          mLastMoveX = x
          mLastMoveY = y
          // DOWN 事件不能返回 handler，因为返回后会直接拦截子 View 事件，导致点击监听失效
          return null
        }
        IPointerEvent.Action.MOVE -> {
          val touchSlop = ViewConfiguration.get(view.context).scaledTouchSlop
          if (abs(x - mLastMoveX) <= touchSlop && abs(y - mLastMoveY) <= touchSlop) {
            mLastMoveX = x
            mLastMoveY = y
            return null
          } else {
            // 超过 touchSlop，可以拦截子 View 事件了
            // 这里返回后就由 AbstractMultiTouchDispatcher 调用 onPointerTouchEvent()
            return mDefaultPointerHandler
          }
        }
        IPointerEvent.Action.UP, IPointerEvent.Action.CANCEL -> {
          // UP 和 CANCEL 是不会回调的
        }
      }
      return null
    }
  }
  
  /**
   * 在没有 handler 处理时，会将事件分发给 [ITouchItem] 或者是 [ScrollTouchHandler] 处理
   */
  class DefaultPointerHandler : IPointerTouchHandler {
  
    private val mItemByPointerId = SparseArray<Pair<ITouchItem, View>>()
    
    override fun onPointerTouchEvent(event: IPointerEvent, view: ViewGroup) {
      val x = event.x.toInt()
      val y = event.y.toInt()
      when (event.action) {
        IPointerEvent.Action.DOWN -> {
          // DOWN 事件是手动传递下来的
          val course = view as ICourseViewGroup
          val pair = course.findPairUnderByXY(x, y)
          if (pair != null && pair.first is ITouchItem && pair.second.isVisible) {
            @Suppress("UNCHECKED_CAST")
            mItemByPointerId.put(event.pointerId, pair as Pair<ITouchItem, View>)
            val item = pair.first
            item.touchHelper.onPointerTouchEvent(event, view, pair.second, item)
          } else {
            ScrollTouchHandler.onPointerTouchEvent(event, view)
          }
        }
        IPointerEvent.Action.MOVE,
        IPointerEvent.Action.UP,
        IPointerEvent.Action.CANCEL -> {
          val pair = mItemByPointerId.get(event.pointerId)
          if (pair != null) {
            pair.first.touchHelper.onPointerTouchEvent(event, view, pair.second, pair.first)
            mItemByPointerId.remove(event.pointerId)
          } else {
            ScrollTouchHandler.onPointerTouchEvent(event, view)
          }
        }
      }
    }
  }
}