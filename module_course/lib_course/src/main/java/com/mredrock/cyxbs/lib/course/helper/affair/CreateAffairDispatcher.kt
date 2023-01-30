package com.mredrock.cyxbs.lib.course.helper.affair

import android.view.MotionEvent
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.fragment.course.expose.fold.FoldState
import com.mredrock.cyxbs.lib.course.fragment.course.expose.wrapper.ICourseWrapper
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ICreateAffair
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchAffairItem
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchCallback
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.utils.forEachReversed
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent.Action.DOWN

/**
 * 长按生成事务的事件分发者
 *
 * ## 注意
 * - 如果你需要添加一些约束性的交互 (比如某时候不允许生成 View)，建议写在 [iCreateAffair] 中
 *
 * @author 985892345
 * @date 2022/9/19 14:48
 */
class CreateAffairDispatcher(
  val page: ICoursePage,
  val iCreateAffair: ICreateAffair = ICreateAffair.Default, // 如果你需要与外界进行交互，建议写在这个里面
) : IPointerDispatcher {
  
  /**
   * 设置点击 [ITouchAffairItem] 的点击监听
   */
  fun setOnClickListener(onClick: ITouchAffairItem.() -> Unit) {
    mOnClickListener = onClick
  }
  
  /**
   * 长按手指移动的回调
   */
  fun addTouchCallback(callback: ITouchCallback) {
    mTouchCallbackImpl.mTouchCallbacks.add(callback)
  }
  
  fun removeTouchCallback(callback: ITouchCallback) {
    mTouchCallbackImpl.mTouchCallbacks.remove(callback)
  }
  
  init {
    page.addCourseLifecycleObservable(
      object : ICourseWrapper.CourseLifecycleObserver {
        override fun onDestroyCourse(course: ICourseViewGroup) {
          mPointerHandlerPool.clear() // Fragment 调用 onDestroyView() 时需要清空池子
        }
      }
    )
  }
  
  // ICreateAffairHandler 的复用池 (因为一个 handler 里面包含对 course 的很多监听，所以采取复用策略)
  private val mPointerHandlerPool = arrayListOf<ICreateAffairHandler>()
  private var mIsAllowIntercept = true
  private var mOnClickListener: (ITouchAffairItem.() -> Unit)? = null
  
  override fun isPrepareToIntercept(event: IPointerEvent, view: ViewGroup): Boolean {
    val x = event.x.toInt()
    val y = event.y.toInt()
    if (event.action == DOWN) {
      if (mIsAllowIntercept) {
        if (iCreateAffair.isValidDown(page, x, y)) {
          // 之后会立即回调 getInterceptHandler()
          return true
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
          )?.first !is ITouchAffairItem
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
   * 生成一个新的 [ICreateAffairHandler]
   */
  private fun getFreeHandler(): ICreateAffairHandler {
    val touchAffairItem = iCreateAffair.createTouchAffairItem(page.course)
    // 这里统一给 TouchAffairItem 设置点击事件
    touchAffairItem?.setOnClickListener { mOnClickListener?.invoke(touchAffairItem) }
    mPointerHandlerPool.forEach {
      // 如果没有被使用，就直接 return
      if (!it.isInUse()) {
        it.setTouchAffairItem(touchAffairItem)
        return it
      }
    }
    val newHandler = ICreateAffairHandler.getImpl(page.course, mTouchCallbackImpl, iCreateAffair)
    mPointerHandlerPool.add(newHandler)
    newHandler.setTouchAffairItem(touchAffairItem)
    return newHandler
  }
  
  /**
   * [ITouchCallback] 的实现类
   */
  private val mTouchCallbackImpl = object : ITouchCallback {
    
    val mTouchCallbacks = arrayListOf<ITouchCallback>()
    
    override fun onLongPressStart(pointerId: Int, initialRow: Int, initialColumn: Int) {
      mTouchCallbacks.forEachReversed { it.onLongPressStart(pointerId, initialRow, initialColumn) }
    }
  
    override fun onTouchMove(
      pointerId: Int,
      initialRow: Int,
      initialColumn: Int,
      touchRow: Int,
      topRow: Int,
      bottomRow: Int,
    ) {
      unfoldNoonOrDuskIfNecessary(initialRow, touchRow)
      mTouchCallbacks.forEachReversed {
        it.onTouchMove(
          pointerId,
          initialRow,
          initialColumn,
          touchRow,
          topRow,
          bottomRow
        )
      }
    }
  
    override fun onTouchEnd(
      pointerId: Int,
      initialRow: Int,
      initialColumn: Int,
      touchRow: Int,
      topRow: Int,
      bottomRow: Int,
    ) {
      mTouchCallbacks.forEachReversed {
        it.onTouchEnd(
          pointerId,
          initialRow,
          initialColumn,
          touchRow,
          topRow,
          bottomRow
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
}