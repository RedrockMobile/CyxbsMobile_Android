package com.mredrock.cyxbs.course.page.find.ui.course.tea

import androidx.core.os.bundleOf
import androidx.fragment.app.createViewModelLazy
import com.mredrock.cyxbs.course.page.course.data.TeaLessonData
import com.mredrock.cyxbs.course.page.find.ui.course.base.BaseFindWeekFragment
import com.mredrock.cyxbs.course.page.find.ui.course.item.TeaLessonItem
import com.mredrock.cyxbs.course.page.find.ui.course.tea.viewmodel.FindTeaCourseViewModel
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 17:12
 */
class FindTeaWeekFragment : BaseFindWeekFragment<TeaLessonData>() {
  
  companion object {
    fun newInstance(week: Int): FindTeaWeekFragment {
      return FindTeaWeekFragment().apply {
        arguments = bundleOf(
          this::mWeek.name to week
        )
      }
    }
  }
  
  override val mWeek by arguments<Int>()
  
  override val mParentViewModel by createViewModelLazy(
    FindTeaCourseViewModel::class,
    { requireParentFragment().viewModelStore }
  )
  
  override fun getLessonItem(list: List<TeaLessonData>): List<ILessonItem> {
    return list.map { TeaLessonItem(it) }
  }
}