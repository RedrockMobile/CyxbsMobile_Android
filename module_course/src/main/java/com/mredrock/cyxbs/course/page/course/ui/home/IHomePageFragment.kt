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
/**
 * 暂时去掉密封类的使用，待R8完全支持后请改回来，原因：
 * 密封类在 D8 和 R8 编译器（产生错误的编译器）中不完全受支持。
 * 在 https://issuetracker.google.com/227160052 中跟踪完全支持的密封类。
 * D8支持将出现在Android Studio Electric Eel中，目前处于预览状态，而R8支持在更高版本之前不会出现
 * stackoverflow上的回答：
 * https://stackoverflow.com/questions/73453524/what-is-causing-this-error-com-android-tools-r8-internal-nc-sealed-classes-are#:~:text=This%20is%20due%20to%20building%20using%20JDK%2017,that%20the%20dexer%20won%27t%20be%20able%20to%20handle.
 */
interface IHomePageFragment : ICoursePage, BaseUi {
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