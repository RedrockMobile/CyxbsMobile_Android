package com.mredrock.cyxbs.course.page.find.ui.course.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.course.page.course.base.CompareWeekSemesterFragment
import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 14:16
 */
abstract class BaseFindSemesterFragment<D : LessonData> : CompareWeekSemesterFragment() {
  
  protected abstract val mParentViewModel: BaseFindViewModel<D>
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initObserve()
  }
  
  private fun initObserve() {
    mParentViewModel.findLessonData
      .observe { map ->
        clearLesson()
        map.values.forEach {
          addLesson(getLessonItem(it))
        }
      }
  }
  
  protected abstract fun getLessonItem(list: List<D>): List<ILessonItem>
}