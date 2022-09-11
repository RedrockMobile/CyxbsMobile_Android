package com.mredrock.cyxbs.lib.course.fragment.course.expose.container

import com.mredrock.cyxbs.lib.course.fragment.course.expose.wrapper.ICourseWrapper
import com.mredrock.cyxbs.lib.course.item.affair.IAffairItem
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 16:11
 */
interface ICourseContainer : ICourseWrapper {
  fun addLesson(lesson: ILessonItem)
  fun addLesson(lessons: List<ILessonItem>)
  fun removeLesson(lesson: ILessonItem)
  fun clearLesson()
  fun getLessonsSize(): Int
  fun containLesson(lesson: ILessonItem?): Boolean
  fun getLessonIterable(): Set<ILessonItem>
  
  fun addAffair(affair: IAffairItem)
  fun addAffair(affairs: List<IAffairItem>)
  fun removeAffair(affair: IAffairItem)
  fun clearAffair()
  fun getAffairsSize(): Int
  fun containAffair(affair: IAffairItem?): Boolean
  fun getAffairIterable(): Set<IAffairItem>
}