package com.mredrock.cyxbs.course.page.find.ui.course.tea

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.mredrock.cyxbs.course.page.course.data.TeaLessonData
import com.mredrock.cyxbs.course.page.find.ui.course.base.BaseFindVpFragment
import com.mredrock.cyxbs.course.page.find.ui.course.tea.viewmodel.FindTeaCourseViewModel
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 17:13
 */
class FindTeaCourseFragment : BaseFindVpFragment<TeaLessonData>() {
  
  companion object {
    /**
     * 尝试替换 Fragment 或者刷新 [teaNum]
     */
    fun tryReplaceOrFresh(fragmentManager: FragmentManager, @IdRes id: Int, teaNum: String) {
      val fragment = fragmentManager.findFragmentById(id) as? FindTeaCourseFragment
      if (fragment == null) {
        FindTeaCourseFragment().apply {
          arguments = bundleOf(
            this::mTeaNum.name to teaNum
          )
          fragmentManager.commit { replace(id, this@apply) }
        }
      } else {
        fragment.tryFreshTeaNum(teaNum)
      }
    }
  }
  
  private fun tryFreshTeaNum(teaNum: String) {
    mTeaNum = teaNum
    mViewModel.tryFreshData(teaNum)
  }
  
  private var mTeaNum by arguments<String>()
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mViewModel.tryFreshData(mTeaNum)
  }
  
  override fun createFragment(position: Int): CoursePageFragment {
    return if (position == 0) FindTeaSemesterFragment()
    else FindTeaWeekFragment.newInstance(position)
  }
  
  override val mViewModel by viewModels<FindTeaCourseViewModel>()
}