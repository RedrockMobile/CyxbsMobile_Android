package com.mredrock.cyxbs.lib.course.fragment.page

import com.mredrock.cyxbs.lib.course.fragment.page.expose.ICourseTouch
import com.mredrock.cyxbs.lib.course.fragment.page.expose.INoLesson
import com.mredrock.cyxbs.lib.course.fragment.page.expose.IToday
import com.mredrock.cyxbs.lib.course.fragment.page.expose.IWeekWrapper

/**
 * page 包下主要放需要跟除课表容器外的其他控件进行交互的实现类
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 17:57
 */
interface ICoursePage :
  ICourseTouch,
  INoLesson,
  IToday,
  IWeekWrapper
{
}
