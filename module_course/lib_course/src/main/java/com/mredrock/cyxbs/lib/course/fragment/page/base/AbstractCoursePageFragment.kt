package com.mredrock.cyxbs.lib.course.fragment.page.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.fragment.course.CourseBaseFragment
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 17:38
 */
abstract class AbstractCoursePageFragment : CourseBaseFragment(), ICoursePage {
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.course_layout_page, container, false)
  }
}