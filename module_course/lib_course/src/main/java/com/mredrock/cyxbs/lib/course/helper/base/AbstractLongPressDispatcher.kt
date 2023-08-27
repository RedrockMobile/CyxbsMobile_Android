package com.mredrock.cyxbs.lib.course.helper.base

import android.graphics.PointF
import android.util.SparseArray
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.utils.getOrPut
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import com.ndhzs.netlayout.touch.multiple.event.pretendEvent
import kotlin.math.abs

/**
 * 用于封装长按逻辑的 [IPointerDispatcher]
 *
 * 该类可以使 [ILongPressTouchHandler] 不必处理长按前的判定逻辑，只关心长按触发后的事件分发
 *
 * ## 整体思路
 * DOWN 事件正常分发，但 MOVE 事件分为两段，一段是长按判定中的 MOVE，该 MOVE 事件将被拦截，
 * 只有长按触发后的 MOVE 事件才会分发给 handler
 *
 * 该方法类似于 https://github.com/985892345-Study/Touch-Study 中 重写 dispatchTouchEvent 法
 *
 * @author 985892345
 * 2023/2/3 10:37
 */
abstract class AbstractLongPressDispatcher : IPointerDispatcher {
  
  /**
   * 触发 DOWN 时的回调，如果需要拦截并开始长按判定，请返回非 null 值
   *
   * ## 注意
   * - 只会收到 DOWN 事件
   *
   * @return 返回 null 时表示不进行拦截
   */
  abstract fun handleEvent(event: IPointerEvent, view: ViewGroup): ILongPressTouchHandler?
  
  /**
   * 长按触发前被取消时的回调
   *
   * 允许返回 [IPointerTouchHandler] 用于继续处理事件，建议返回用于滑动滚轴的 handler
   *
   * 可以接收到 MOVE、UP、CANCEL
   */
  open fun onCancelLongPress(
    event: IPointerEvent,
    view: ViewGroup,
    initialX: Int,
    initialY: Int
  ): IPointerTouchHandler? = null
  
  private val mLongPressManagerByPointerId = SparseArray<LongPressManager>()
  
  private val mInitialPointByPointerId = SparseArray<PointF>()
  private val mLastMovePointByPointerId = SparseArray<PointF>()
  
  final override fun isPrepareToIntercept(event: IPointerEvent, view: ViewGroup): Boolean {
    when (event.action) {
      IPointerEvent.Action.DOWN -> {
        val x = event.x
        val y = event.y
        
        val initialPoint = mInitialPointByPointerId.getOrPut(event.pointerId) { PointF() }
        initialPoint.x = x
        initialPoint.y = y
        
        val lastMovePoint = mLastMovePointByPointerId.getOrPut(event.pointerId) { PointF() }
        lastMovePoint.x = x
        lastMovePoint.y = y
        
        val handler = handleEvent(event, view)
        if (handler != null) {
          // 正常逻辑下 DOWN 事件是不会主动回调 handler 的，所以需要手动调用
          handler.onPointerTouchEvent(event, view)
          val manager = LongPressManager(view, handler)
          mLongPressManagerByPointerId.put(event.pointerId, manager)
          manager.startLongPress()
          return true
        }
      }
      IPointerEvent.Action.UP, IPointerEvent.Action.CANCEL -> {
        // 在长按延时前手指抬起，应该传递 CANCEL 给 handler
        val manager = mLongPressManagerByPointerId.get(event.pointerId)
        manager.cancelLongPress(event)
        mLongPressManagerByPointerId.remove(event.pointerId)
        
        val initialPoint = mInitialPointByPointerId.get(event.pointerId)
        onCancelLongPress(event, view, initialPoint.x.toInt(), initialPoint.y.toInt())
      }
      IPointerEvent.Action.MOVE -> {
        // move 事件不在这里处理，应该在 getInterceptHandler() 中处理
      }
    }
    return false
  }
  
  final override fun getInterceptHandler(
    event: IPointerEvent,
    view: ViewGroup
  ): IPointerTouchHandler? {
    val manager = mLongPressManagerByPointerId.get(event.pointerId)
    val handler = manager.tryGetHandler()
    if (handler != null) {
      mLongPressManagerByPointerId.remove(event.pointerId)
      return handler
    }
    // handler 为 null 时说明处于长按延时内或者已经取消了长按
    
    val touchSlop = ViewConfiguration.get(view.context).scaledTouchSlop
    val point = mLastMovePointByPointerId.get(event.pointerId)
      ?: return null // 如果 point 为 null，这说明已经取消了长按
    val x = event.x
    val y = event.y
    val lastMoveX = point.x
    val lastMoveY = point.y
    
    if (abs(x - lastMoveX) > touchSlop
      || abs(y - lastMoveY) > touchSlop
    ) {
      manager.cancelLongPress(event)
      mLastMovePointByPointerId.remove(event.pointerId) // 以 point 是否存在表示长按是否被取消
      val initialPoint = mInitialPointByPointerId.get(event.pointerId)
      return onCancelLongPress(event, view, initialPoint.x.toInt(), initialPoint.y.toInt())
    }
    point.x = x
    point.y = y
    return manager.tryGetHandler()
  }
  
  private class LongPressManager(
    private val view: ViewGroup,
    private val handler: ILongPressTouchHandler
  ) {
    
    private var mIsInLongPress = false
    
    private val mLongPressRunnable = object : Runnable {
      override fun run() {
        longPressed()
      }
      
      fun start() {
        view.postDelayed(this, ViewConfiguration.getLongPressTimeout().toLong())
      }
      
      fun cancel() {
        view.removeCallbacks(this)
      }
    }
    
    private fun longPressed() {
      mIsInLongPress = true
      handler.onLongPressed()
    }
    
    /**
     * 尝试得到 handler
     *
     * 如果此时没有处于长按已经激活状态，则返回 null
     */
    fun tryGetHandler(): ILongPressTouchHandler? {
      return if (mIsInLongPress) handler else null
    }
    
    fun startLongPress() {
      mLongPressRunnable.start()
    }
    
    fun cancelLongPress(event: IPointerEvent) {
      mLongPressRunnable.cancel()
      event.pretendEvent(MotionEvent.ACTION_CANCEL) {
        handler.onPointerTouchEvent(it, view)
      }
    }
  }
}