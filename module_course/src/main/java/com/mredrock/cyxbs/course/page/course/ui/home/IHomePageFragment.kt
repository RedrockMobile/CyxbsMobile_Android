package com.mredrock.cyxbs.course.page.course.ui.home

import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.course.page.course.utils.container.AffairContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.LinkLessonContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.SelfLessonContainerProxy
import com.mredrock.cyxbs.lib.base.ui.BaseUi

/**
 * .
 *
 * @author 985892345
 * 2023/2/23 13:18
 */
sealed interface IHomePageFragment : BaseUi {
  val parentViewModel: HomeCourseViewModel
  val selfLessonContainerProxy: SelfLessonContainerProxy
  val linkLessonContainerProxy: LinkLessonContainerProxy
  val affairContainerProxy: AffairContainerProxy
}