package com.mredrock.cyxbs.lib.course.fragment.page

import com.mredrock.cyxbs.lib.course.fragment.page.base.WeekWrapperImpl
import com.mredrock.cyxbs.lib.course.fragment.vp.AbstractCourseVpFragment
import com.mredrock.cyxbs.lib.course.fragment.vp.AbstractHeaderCourseVpFragment

/**
 * 每一页的课表模板
 *
 * 考虑到课表有整学期界面和每一周的页面，具体请查看 [CourseSemesterFragment] 和 [CourseWeekFragment]
 *
 * ```
 * 我推荐的课表框架为：
 *                                               *CourseVpFragment
 *                                                      ↓
 *            +---------------------------+---------------------------+---------------------------+
 *            ↓                           ↓                           ↓                           ↓
 * CourseSemesterFragment         CourseWeekFragment          CourseWeekFragment         CourseWeekFragment
 *
 * ```
 * - `*CourseVpFragment` 指 [AbstractCourseVpFragment] 或者 [AbstractHeaderCourseVpFragment] 的子类
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 18:16
 */
open class CoursePageFragment : WeekWrapperImpl(), ICoursePage {
}