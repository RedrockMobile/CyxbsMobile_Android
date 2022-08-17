package com.mredrock.cyxbs.lib.course.lesson

import com.mredrock.cyxbs.lib.course.period.am.IAmPeriod
import com.mredrock.cyxbs.lib.course.period.night.INightPeriod
import com.mredrock.cyxbs.lib.course.period.pm.IPmPeriod

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 15:28
 */
interface ILessonContainer : IAmPeriod, IPmPeriod, INightPeriod {
  fun addLesson(lesson: ILesson)
}