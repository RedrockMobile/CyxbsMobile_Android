package lib.course.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment
import com.mredrock.cyxbs.lib.course.fragment.vp.AbstractCourseVpFragment
import lib.course.ui.viewmodel.VpViewModel

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/26 17:34
 */
class CourseVpFragment : AbstractCourseVpFragment() {
  
  val viewModel by viewModels<VpViewModel>()
  
  override val mPageCount: Int
    get() = 22
  
  override fun createFragment(position: Int): CoursePageFragment {
    return if (position == 0) CourseSemesterFragment() else CourseWeekFragment.newInstance(position)
  }
  
  override val mViewPager: ViewPager2
    get() = requireView() as ViewPager2
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return ViewPager2(requireContext())
  }
}