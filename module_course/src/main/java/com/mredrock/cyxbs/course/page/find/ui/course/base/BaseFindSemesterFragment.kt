package com.mredrock.cyxbs.course.page.find.ui.course.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.course.page.course.base.CompareWeekSemesterFragment
import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem

/**
 * 查看他人课表的整学期界面
 *
 * 由于查找学生和老师课表的功能几乎一模一样，但又不想写在同一个 Fragment 中，所以写了该抽象类
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 14:16
 */
abstract class BaseFindSemesterFragment<D : LessonData> : CompareWeekSemesterFragment() {
  
  /**
   * 得到宿主的 [BaseFindViewModel]
   *
   * 正确的写法：
   * ```
   * override val mParentViewModel by createViewModelLazy(
   *   FindStuCourseViewModel::class,                  // 注意：这里要写具体的实现类
   *   { requireParentFragment().viewModelStore }
   * )
   * ```
   */
  protected abstract val mParentViewModel: BaseFindViewModel<D>
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initObserve()
  }
  
  private fun initObserve() {
    mParentViewModel.findLessonData
      .observe {
        clearLesson()
        addLesson(it.mapToMinWeek().getLessonItem())
      }
  }
  
  /**
   * 得到数据对应的 [ILessonItem]
   */
  protected abstract fun List<D>.getLessonItem(): List<ILessonItem>
}