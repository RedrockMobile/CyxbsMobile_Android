package com.mredrock.cyxbs.lib.course.fragment.course

import com.mredrock.cyxbs.lib.course.fragment.course.expose.container.ICourseContainer
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlapContainer
import com.mredrock.cyxbs.lib.course.fragment.course.expose.fold.IFoldDusk
import com.mredrock.cyxbs.lib.course.fragment.course.expose.fold.IFoldNoon
import com.mredrock.cyxbs.lib.course.fragment.course.expose.period.ICoursePeriod
import com.mredrock.cyxbs.lib.course.fragment.course.expose.wrapper.ICourseWrapper
import com.mredrock.cyxbs.lib.course.fragment.course.expose.wrapper.IScrollWrapper
import com.mredrock.cyxbs.lib.course.fragment.course.expose.timeline.ITimeline

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 15:43
 */
interface ICourseBase :
  ICourseWrapper, // 包裹 CourseViewGroup
  IScrollWrapper, // 包裹 CourseScroll
  ICourseContainer, // 课表容器
  IFoldNoon, // 折叠中午时间段
  IFoldDusk, // 折叠傍晚时间段
  IOverlapContainer, // 用于实现重叠的逻辑
  ICoursePeriod, // 课表时间区域
  ITimeline // 左侧时间轴
{
}