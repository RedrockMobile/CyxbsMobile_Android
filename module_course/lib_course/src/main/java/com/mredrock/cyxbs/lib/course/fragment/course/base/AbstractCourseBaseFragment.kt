package com.mredrock.cyxbs.lib.course.fragment.course.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.fragment.course.ICourseBase
import com.mredrock.cyxbs.lib.course.fragment.course.expose.wrapper.ICourseWrapper
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.internal.view.course.base.AbstractCourseViewGroup
import com.mredrock.cyxbs.lib.course.internal.view.scroll.ICourseScroll
import com.mredrock.cyxbs.lib.course.internal.view.scroll.base.AbstractCourseScrollView
import com.mredrock.cyxbs.lib.course.utils.forEachReversed

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 15:00
 */
@Suppress("LeakingThis")
abstract class AbstractCourseBaseFragment : BaseFragment(), ICourseBase {
  
  // CourseViewGroup 保持接口通信，降低耦合度
  override val scroll: ICourseScroll by R.id.course_scrollView.view<AbstractCourseScrollView>()
  override val course: ICourseViewGroup by R.id.course_courseLayout.view<AbstractCourseViewGroup>()
  
  /**
   * 你可以重写这个方法
   *
   * 但你需要保证里面有对应的 id，可以使用 <include/> 来包含 layout 文件进去
   *
   * 但如果你想更进一步的修改里面的东西，分为以下几种情况
   * - 简单修改，并且全部课表界面都需要（比如全部页面都修改中午时间段为两行）：直接在布局里面动刀即可
   * - 简单修改，但只有我自己的页面要用（我的页面不需要中午和傍晚时间段）：比较复杂的话就在你自己的模块中继承并修改，简单的话用代码即可
   * - 复杂修改：建议实现对应接口重写课表
   */
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    // 最基础布局，不建议对他进行修改，但允许你在它外面添加东西
    return inflater.inflate(R.layout.course_layout_course, container, false)
  }
  
  final override fun isCourseDestroyed(): Boolean {
    return view == null
  }
  
  private val mCourseLifecycleObservers = arrayListOf<ICourseWrapper.CourseLifecycleObserver>()
  
  final override fun addCourseLifecycleObservable(
    observer: ICourseWrapper.CourseLifecycleObserver,
    isCallbackIfCrested: Boolean
  ) {
    mCourseLifecycleObservers.add(observer)
    if (isCallbackIfCrested && view != null) {
      // 但根布局不为 null 时，说明已经调用 onViewCreated()
      observer.onCreateCourse(course)
    }
  }
  
  final override fun removeCourseLifecycleObserver(observer: ICourseWrapper.CourseLifecycleObserver) {
    mCourseLifecycleObservers.remove(observer)
  }
  
  final override fun doOnCourseCreate(action: ICourseViewGroup.() -> Unit, isCallbackIfCreated: Boolean) {
    addCourseLifecycleObservable(
      object : ICourseWrapper.CourseLifecycleObserver {
        override fun onCreateCourse(course: ICourseViewGroup) {
          action.invoke(course)
        }
      }, isCallbackIfCreated
    )
  }
  
  final override fun doOnCourseDestroy(action: ICourseViewGroup.() -> Unit) {
    addCourseLifecycleObservable(
      object : ICourseWrapper.CourseLifecycleObserver {
        override fun onDestroyCourse(course: ICourseViewGroup) {
          action.invoke(course)
        }
      }
    )
  }
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mCourseLifecycleObservers.forEachReversed {
      it.onCreateCourse(course)
    }
  }
  
  @CallSuper
  override fun onDestroyView() {
    super.onDestroyView()
    mCourseLifecycleObservers.forEachReversed {
      it.onDestroyCourse(course)
    }
  }
}