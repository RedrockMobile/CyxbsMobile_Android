package lib.course.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.createViewModelLazy
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment
import lib.course.item.SelfLessonItem
import lib.course.ui.viewmodel.VpViewModel

class CourseSemesterFragment : CoursePageFragment() {
  
  private val viewModel by createViewModelLazy(
    VpViewModel::class,
    { requireParentFragment().viewModelStore }
  )
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.selfLessons
      .collectLaunch {
        it.forEach { entry ->
          addLesson(entry.value.map { data -> SelfLessonItem(data) })
        }
      }
  }
}