package com.mredrock.cyxbs.lib.course.helper.affair

import android.view.MotionEvent
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.fragment.course.expose.fold.FoldState
import com.mredrock.cyxbs.lib.course.fragment.course.expose.wrapper.ICourseWrapper
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent.Action.DOWN

/**
 * .
 *
 * @author 985892345
 * @date 2022/9/19 14:48
 */
class CreateAffairDispatcher(
  val page: ICoursePage
) : IPointerDispatcher, CreateAffairHandler.ITouch {
  
  init {
    page.addCourseLifecycleObservable(
      object : ICourseWrapper.CourseLifecycleObserver {
        override fun onCreateCourse(course: ICourseViewGroup) {}
        override fun onDestroyCourse(course: ICourseViewGroup) {
          mPointerHandlerPool.clear() // Fragment 调用 onDestroy() 时需要清空池子
        }
      }, false
    )
  }
  
  private val mPointerHandlerPool = arrayListOf<ICreateAffairHandler>()
  private var mIsAllowIntercept = true
  
  override fun isPrepareToIntercept(event: IPointerEvent, view: ViewGroup): Boolean {
    val x = event.x.toInt()
    val y = event.y.toInt()
    if (event.action == DOWN) {
      if (mIsAllowIntercept) {
        if (x > page.getTimelineEndWidth()) {
          if (page.course.findPairUnderByXY(x, y) == null) {
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
  
  private fun getFreeHandler(): ICreateAffairHandler {
    mPointerHandlerPool.forEach {
      if (!it.isInUse()) return it
    }
    val newHandler = ICreateAffairHandler.getImpl(page.course, ITouchAffair.getImpl(page.course), this)
    mPointerHandlerPool.add(newHandler)
    return newHandler
  }
  
  override fun onLongPressStart(pointerId: Int, initialRow: Int, initialColumn: Int) {
  
  }
  
  override fun onMove(pointerId: Int, initialRow: Int, initialColumn: Int, touchRow: Int) {
    unfoldNoonOrDuskIfNecessary(initialRow, touchRow)
  }
  
  override fun onUp(pointerId: Int, initialRow: Int, initialColumn: Int) {
  
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