package com.mredrock.cyxbs.course.page.find.ui.course.stu

import androidx.core.os.bundleOf
import androidx.fragment.app.createViewModelLazy
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.find.ui.course.base.BaseFindWeekFragment
import com.mredrock.cyxbs.course.page.find.ui.course.item.StuLessonItem
import com.mredrock.cyxbs.course.page.find.ui.course.stu.viewmodel.FindStuCourseViewModel
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 16:52
 */
class FindStuWeekFragment : BaseFindWeekFragment<StuLessonData>() {
  
  companion object {
    fun newInstance(week: Int): FindStuWeekFragment {
      return FindStuWeekFragment().apply {
        arguments = bundleOf(
          this::mWeek.name to week
        )
      }
    }
  }
  
  override val mWeek by arguments<Int>()
  
  override val mParentViewModel by createViewModelLazy(
    FindStuCourseViewModel::class,
    { requireParentFragment().viewModelStore }
  )
  
  override fun getLessonItem(list: List<StuLessonData>): List<ILessonItem> {
    return list.map { StuLessonItem(it) }
  }
}