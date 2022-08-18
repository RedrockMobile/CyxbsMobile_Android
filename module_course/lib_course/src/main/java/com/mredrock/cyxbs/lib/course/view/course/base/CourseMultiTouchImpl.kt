package com.mredrock.cyxbs.lib.course.view.course.base

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.touch.IMultiTouch
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
  
  private inner class CourseMultiTouchDispatcherHelper : MultiTouchDispatcherHelper() {
    override fun getDefaultTouchHandler(
      event: IPointerEvent,
      view: ViewGroup
    ): IPointerTouchHandler? {
      return mDefaultHandler?.getDefaultPointerHandler(event, view)
    }
  }
}