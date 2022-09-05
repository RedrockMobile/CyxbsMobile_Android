package com.mredrock.cyxbs.lib.course.fragment.course.expose.container

import com.mredrock.cyxbs.lib.course.fragment.item.IAffair
import com.mredrock.cyxbs.lib.course.fragment.item.ILesson

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 16:11
 */
interface ICourseContainer {
  fun addLesson(lesson: ILesson)
  fun addLesson(lessons: List<ILesson>)
  fun removeLesson(lesson: ILesson)
  fun clearLesson()
  
  fun addAffair(affair: IAffair)
  fun addAffair(affairs: List<IAffair>)
  fun removeAffair(affair: IAffair)
  fun clearAffair()
}