package com.mredrock.cyxbs.course.page.find.ui.course.tea

import androidx.fragment.app.createViewModelLazy
import com.mredrock.cyxbs.course.page.course.data.TeaLessonData
import com.mredrock.cyxbs.course.page.find.ui.course.base.BaseFindSemesterFragment
import com.mredrock.cyxbs.course.page.find.ui.course.item.TeaLessonItem
import com.mredrock.cyxbs.course.page.find.ui.course.tea.viewmodel.FindTeaCourseViewModel
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 17:11
 */
class FindTeaSemesterFragment : BaseFindSemesterFragment<TeaLessonData>() {
  
  override val mParentViewModel by createViewModelLazy(
    FindTeaCourseViewModel::class,
    { requireParentFragment().viewModelStore }
  )
  
  override fun List<TeaLessonData>.getLessonItem(): List<ILessonItem> {
    return map { TeaLessonItem(it) }
  }
}