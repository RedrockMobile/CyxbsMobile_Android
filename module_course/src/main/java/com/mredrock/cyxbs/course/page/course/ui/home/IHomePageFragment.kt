package com.mredrock.cyxbs.course.page.course.ui.home

import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.course.page.course.utils.container.AffairContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.LinkLessonContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.SelfLessonContainerProxy
import com.mredrock.cyxbs.lib.base.ui.BaseUi
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage

/**
 * 因为 [HomeSemesterFragment] 和 [HomeWeekFragment] 是分开的两个类，
 * 但他们之间有很多东西都是相同的，所以提供该密封接口供外部使用
 *
 * @author 985892345
 * 2023/2/23 13:18
 */
sealed interface IHomePageFragment : ICoursePage, BaseUi {
  /**
   * 页面周数，如果为 0 则为整学期
   */
  val week: Int
  
  /**
   * 父 Fragment 的 ViewModel，因为页面在 VP2 中
   */
  val parentViewModel: HomeCourseViewModel
  
  /**
   * 管理自己课程的容器代理类
   */
  val selfLessonContainerProxy: SelfLessonContainerProxy
  
  /**
   * 管理关联人课程的容器代理类
   */
  val linkLessonContainerProxy: LinkLessonContainerProxy
  
  /**
   * 管理事务的容器代理类
   */
  val affairContainerProxy: AffairContainerProxy
}