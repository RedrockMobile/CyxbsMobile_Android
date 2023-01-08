package com.mredrock.cyxbs.lib.course.fragment.page.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.course.fragment.page.expose.ICourseTouch
import com.mredrock.cyxbs.lib.course.helper.CourseDownAnimDispatcher
import com.mredrock.cyxbs.lib.course.helper.ScrollTouchHandler
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 18:23
 */
abstract class CourseTouchImpl : AbstractCoursePageFragment(), ICourseTouch {
  
  override fun getDefaultPointerHandler(event: IPointerEvent, view: View): IPointerTouchHandler? {
    // 如果是第一根手指的事件在没有其他分发者拦截时默认交给 ScrollTouchHandler 拦截
    return if (event.pointerId != 0) ScrollTouchHandler else null
  }
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    course.setDefaultHandler(this)
    val dispatcher = getCourseDownAnimDispatcher()
    if (dispatcher != null) {
      course.addPointerDispatcher(dispatcher)
    }
  }
  
  override fun getCourseDownAnimDispatcher(): CourseDownAnimDispatcher? {
    return CourseDownAnimDispatcher(course)
  }
}