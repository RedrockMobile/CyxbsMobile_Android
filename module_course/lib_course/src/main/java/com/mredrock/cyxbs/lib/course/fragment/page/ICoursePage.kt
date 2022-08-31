package com.mredrock.cyxbs.lib.course.fragment.page

import com.mredrock.cyxbs.lib.course.fragment.page.expose.ICourseTouch
import com.mredrock.cyxbs.lib.course.fragment.page.expose.INoLesson
import com.mredrock.cyxbs.lib.course.fragment.page.expose.IWeekWrapper

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 17:57
 */
interface ICoursePage :
  ICourseTouch,
  IWeekWrapper,
  INoLesson
{
}
