package com.mredrock.cyxbs.course.page.find.ui.course.stu

import androidx.fragment.app.createViewModelLazy
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.find.ui.course.base.BaseFindSemesterFragment
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
class FindStuSemesterFragment : BaseFindSemesterFragment<StuLessonData>() {
  
  override val mParentViewModel by createViewModelLazy(
    FindStuCourseViewModel::class,
    { requireParentFragment().viewModelStore }
  )
  
  override fun List<StuLessonData>.getLessonItem(): List<ILessonItem> {
    return map { StuLessonItem(it) }
  }
}