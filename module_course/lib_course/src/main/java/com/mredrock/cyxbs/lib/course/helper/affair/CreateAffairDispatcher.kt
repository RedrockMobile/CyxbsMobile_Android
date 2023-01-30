package com.mredrock.cyxbs.lib.course.helper.affair

import android.view.MotionEvent
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.fragment.course.expose.fold.FoldState
import com.mredrock.cyxbs.lib.course.fragment.course.expose.wrapper.ICourseWrapper
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.utils.forEachReversed
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent.Action.DOWN

/**
 * 长按生成事务的事件分发者
 *
 * @author 985892345
 * @date 2022/9/19 14:48
 */
class CreateAffairDispatcher(
  val page: ICoursePage,
) : IPointerDispatcher, ICreateAffairHandler.TouchCallback {
  
  fun setOnClickListener(onClick: ITouchAffair.() -> Unit) {
    mOnClickListener = onClick
  }
  
  fun addTouchCallback(callback: ICreateAffairHandler.TouchCallback) {
    mTouchCallbacks.add(callback)
  }
  
  fun removeTouchCallback(callback: ICreateAffairHandler.TouchCallback) {
    mTouchCallbacks.remove(callback)
  }
  
  init {
    page.addCourseLifecycleObservable(
      object : ICourseWrapper.CourseLifecycleObserver {
        override fun onDestroyCourse(course: ICourseViewGroup) {
          mPointerHandlerPool.clear() // Fragment 调用 onDestroy() 时需要清空池子
        }
      }
    )
  }
  
  // ICreateAffairHandler 的复用池
  private val mPointerHandlerPool = arrayListOf<ICreateAffairHandler>()
  private var mIsAllowIntercept = true
  private var mOnClickListener: (ITouchAffair.() -> Unit)? = null
  private val mTouchCallbacks = arrayListOf<ICreateAffairHandler.TouchCallback>()
  
  override fun isPrepareToIntercept(event: IPointerEvent, view: ViewGroup): Boolean {
    val x = event.x.toInt()
    val y = event.y.toInt()
    if (event.action == DOWN) {
      if (mIsAllowIntercept) {
        if (x > page.getTimelineEndWidth()) {
          // 触摸位置大于左边时间轴的宽度时
          if (page.course.findPairUnderByXY(x, y) == null) {
            // 当前触摸的是空白位置时才准备拦截，之后会立即回调 getInterceptHandler()
            return true
          }
        }
      }
    }
    return false
  }
  
  override fun getInterceptHandler(event: IPointerEvent, view: ViewGroup): IPointerTouchHandler {
    return getFreeHandler()
  }
  
  override fun onDispatchTouchEvent(event: MotionEvent, view: ViewGroup) {
    super.onDispatchTouchEvent(event, view)
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        mIsAllowIntercept = true // 还原
        if (page.course.findPairUnderByXY(
            event.x.toInt(),
            event.y.toInt()
          )?.first !is ITouchAffair
        ) {
          // DOWN 事件时如果点击的不是 TouchAffair，则取消已经显示的 TouchAffair
          mPointerHandlerPool.forEach {
            if (it.isInShow()) {
              mIsAllowIntercept = false // 如果存在正在显示的 TouchAffair，则这次 DOWN 事件不拦截
              it.cancelShow()
            }
          }
        }
      }
      MotionEvent.ACTION_POINTER_DOWN -> {
        mIsAllowIntercept = true // 但第二跟手指触摸时就允许拦截
      }
    }
  }
  
  /**
   * 得到空闲的 ICreateAffairHandler 或者生成一个新的 ICreateAffairHandler
   */
  private fun getFreeHandler(): ICreateAffairHandler {
    mPointerHandlerPool.forEach {
      // 如果没有被使用，就直接 return
      if (!it.isInUse()) return it
    }
    val newHandler = ICreateAffairHandler.getImpl(
      page.course,
      ITouchAffair
        .getImpl(page.course)
        .apply { setOnClickListener { mOnClickListener?.invoke(this) } },
      this
    )
    mPointerHandlerPool.add(newHandler)
    return newHandler
  }
  
  override fun onLongPressStart(pointerId: Int, initialRow: Int, initialColumn: Int) {
    mTouchCallbacks.forEachReversed { it.onLongPressStart(pointerId, initialRow, initialColumn) }
  }
  
  override fun onTouchMove(
    pointerId: Int,
    initialRow: Int,
    initialColumn: Int,
    topRow: Int,
    bottomRow: Int,
    touchRow: Int,
  ) {
    unfoldNoonOrDuskIfNecessary(initialRow, touchRow)
    mTouchCallbacks.forEachReversed {
      it.onTouchMove(
        pointerId,
        initialRow,
        initialColumn,
        topRow,
        bottomRow,
        touchRow
      )
    }
  }
  
  override fun onTouchEnd(
    pointerId: Int,
    initialRow: Int,
    initialColumn: Int,
    topRow: Int,
    bottomRow: Int,
    touchRow: Int,
  ) {
    mTouchCallbacks.forEachReversed {
      it.onTouchEnd(
        pointerId,
        initialRow,
        initialColumn,
        topRow,
        bottomRow,
        touchRow
      )
    }
  }
  
  /**
   * 判断当前滑动中是否需要自动展开中午或者傍晚时间段
   * @param initialRow 最开始触摸的行数
   * @param touchRow 当前触摸的行数
   */
  private fun unfoldNoonOrDuskIfNecessary(initialRow: Int, touchRow: Int) {
    when (page.getNoonRowState()) {
      FoldState.FOLD, FoldState.FOLD_ANIM, FoldState.UNKNOWN -> {
        if (page.compareNoonPeriod(initialRow) * page.compareNoonPeriod(touchRow) <= 0) {
          page.unfoldNoon()
        }
      }
      else -> {}
    }
    when (page.getDuskRowState()) {
      FoldState.FOLD, FoldState.FOLD_ANIM, FoldState.UNKNOWN -> {
        if (page.compareDuskPeriod(initialRow) * page.compareDuskPeriod(touchRow) <= 0) {
          page.unfoldDusk()
        }
      }
      else -> {}
    }
  }
}