package com.mredrock.cyxbs.lib.course.fragment.course.expose.wrapper

import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 15:39
 */
interface ICourseWrapper {
  
  val course: ICourseViewGroup
  
  fun addCourseLifecycleObservable(observer: CourseLifecycleObserver)
  
  fun removeCourseLifecycleObserver(observer: CourseLifecycleObserver)
  
  /**
   * 解决 Fragment 与 View 生命周期不一致的问题
   */
  interface CourseLifecycleObserver {
    fun onCreateCourse(course: ICourseViewGroup)
    fun onDestroyCourse(course: ICourseViewGroup)
  }
}