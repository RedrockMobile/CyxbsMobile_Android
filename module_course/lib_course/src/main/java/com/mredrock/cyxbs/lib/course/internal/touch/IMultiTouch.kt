package com.mredrock.cyxbs.lib.course.internal.touch

import android.view.View
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * 添加多指触摸分发的接口
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 12:51
 */
interface IMultiTouch {
  
  /**
   * 添加多指触摸分发者
   */
  fun addPointerDispatcher(dispatcher: IPointerDispatcher)
  
  /**
   * 设置默认的手指处理者
   */
  fun setDefaultHandler(handler: DefaultHandler?)
  
  /**
   * 得到 [pointerId] 对应的 [IPointerTouchHandler]
   */
  fun getTouchHandler(pointerId: Int): IPointerTouchHandler?
  
  /**
   * 默认多指处理者，在当前 PointerId 对应的事件没有处理者拦截时触发
   */
  fun interface DefaultHandler {
    fun getDefaultPointerHandler(event: IPointerEvent, view: View): IPointerTouchHandler?
  }
}