package com.mredrock.cyxbs.course.page.course.ui.home

import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.course.page.course.utils.container.AffairContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.LinkLessonContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.SelfLessonContainerProxy
import com.mredrock.cyxbs.lib.base.ui.BaseUi

/**
 * 因为 [HomeSemesterFragment] 和 [HomeWeekFragment] 是分开的两个类，
 * 但他们之间有很多东西都是相同的，所以提供该密封接口供外部使用
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