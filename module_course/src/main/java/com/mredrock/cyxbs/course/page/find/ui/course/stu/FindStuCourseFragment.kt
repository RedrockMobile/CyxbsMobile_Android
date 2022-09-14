package com.mredrock.cyxbs.course.page.find.ui.course.stu

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.find.ui.course.base.BaseFindVpFragment
import com.mredrock.cyxbs.course.page.find.ui.course.stu.viewmodel.FindStuCourseViewModel
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 16:52
 */
class FindStuCourseFragment : BaseFindVpFragment<StuLessonData>() {
  
  companion object {
    /**
     * 尝试替换 Fragment 或者刷新 [stuNum]
     */
    fun tryReplaceOrFresh(fragmentManager: FragmentManager, @IdRes id: Int, stuNum: String) {
      val fragment = fragmentManager.findFragmentById(id) as? FindStuCourseFragment
      if (fragment == null) {
        FindStuCourseFragment().apply {
          arguments = bundleOf(
            this::mStuNum.name to stuNum
          )
          fragmentManager.commit { replace(id, this@apply) }
        }
      } else {
        fragment.tryFreshTeaNum(stuNum)
      }
    }
  }
  
  private fun tryFreshTeaNum(stuNum: String) {
    mStuNum = stuNum
    mViewModel.tryFreshData(stuNum)
  }
  
  private var mStuNum by arguments<String>()
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mViewModel.tryFreshData(mStuNum)
  }
  
  override fun createFragment(position: Int): CoursePageFragment {
    return if (position == 0) FindStuSemesterFragment()
    else FindStuWeekFragment.newInstance(position)
  }
  
  override val mViewModel by viewModels<FindStuCourseViewModel>()
}