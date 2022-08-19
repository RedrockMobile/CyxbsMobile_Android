package com.mredrock.cyxbs.lib.course.internal.view.course.base

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.internal.touch.IMultiTouch
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.MultiTouchDispatcherHelper
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 12:39
 */
abstract class CourseMultiTouchImpl @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = R.attr.courseLayoutStyle,
  defStyleRes: Int = 0
) : CourseLayoutParamHandler(context, attrs, defStyleAttr, defStyleRes), IMultiTouch {
  
  private val mMultiTouchDispatcherHelper = CourseMultiTouchDispatcherHelper()
  private var mDefaultHandler: IMultiTouch.DefaultHandler? = null
  
  final override fun addPointerDispatcher(dispatcher: IPointerDispatcher) {
    mMultiTouchDispatcherHelper.addPointerDispatcher(dispatcher)
  }
  
  final override fun setDefaultHandler(handler: IMultiTouch.DefaultHandler?) {
    mDefaultHandler = handler
  }
  
  @CallSuper
  override fun onFinishInflate() {
    super.onFinishInflate()
    /*
    * 设置 POINTER_DOWN 事件只会传递给单一的 View
    * 主要是防止你手指正在长按移动课程，但另一个手指却点击了关联或者回到本周的按钮，
    * 此时不会触发 CANCEL 事件，从而导致 vp 数据变化而闪退
    * */
    var parent = parent
    while (parent is ViewGroup) {
      // 这个 isMotionEventSplittingEnabled 在 dispatchTouchEvent() 中有用到，
      // 你看官方注释的话应该能看懂它的作用，事件分发源码从没看过? 那我不建议你来修课表 (
      parent.isMotionEventSplittingEnabled = false
      parent = parent.parent
    }
  }
  
  init {
    addItemTouchListener(mMultiTouchDispatcherHelper)
  }
  
  private inner class CourseMultiTouchDispatcherHelper : MultiTouchDispatcherHelper() {
    override fun getDefaultTouchHandler(
      event: IPointerEvent,
      view: ViewGroup
    ): IPointerTouchHandler? {
      return mDefaultHandler?.getDefaultPointerHandler(event, view)
    }
  }
}