package com.mredrock.cyxbs.lib.course.fragment.page.course

import com.mredrock.cyxbs.lib.course.fragment.page.period.ICoursePeriod
import com.mredrock.cyxbs.lib.course.fragment.page.period.dusk.IFoldDusk
import com.mredrock.cyxbs.lib.course.fragment.page.period.noon.IFoldNoon
import com.mredrock.cyxbs.lib.course.fragment.page.timeline.ITimeLine
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup

/**
 * 课表业务逻辑扩展接口
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 15:24
 */
interface ICourseExtend : ICoursePeriod,
  ITimeLine, // 左侧时间轴
  IFoldNoon, IFoldDusk, // 中午和傍晚的折叠逻辑
  ICourseContainer // 课表容器
{
  val course: ICourseViewGroup
}