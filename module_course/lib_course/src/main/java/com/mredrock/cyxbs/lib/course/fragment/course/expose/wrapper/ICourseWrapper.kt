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
  
  /**
   * 添加 [ICourseViewGroup] 生命周期的监听
   * @param isCallbackIfCrested 如果添加监听时已经创建，则是否需要立马回调
   */
  fun addCourseLifecycleObservable(observer: CourseLifecycleObserver, isCallbackIfCrested: Boolean = false)
  
  /**
   * 移除 [ICourseViewGroup] 生命周期的监听
   */
  fun removeCourseLifecycleObserver(observer: CourseLifecycleObserver)
  
  /**
   * 解决 Fragment 与 View 生命周期不一致的问题
   */
  interface CourseLifecycleObserver {
    /**
     * 创建了 [ICourseViewGroup] 的回调
     */
    fun onCreateCourse(course: ICourseViewGroup) {}
  
    /**
     * [ICourseViewGroup] 即将被摧毁时的回调
     */
    fun onDestroyCourse(course: ICourseViewGroup) {}
  }
}