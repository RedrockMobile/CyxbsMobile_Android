package com.mredrock.cyxbs.lib.course.fragment.course.base

import com.mredrock.cyxbs.lib.course.fragment.course.expose.container.ICourseContainer
import com.mredrock.cyxbs.lib.course.item.affair.IAffairItem
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 15:36
 */
abstract class ContainerImpl : AbstractCourseBaseFragment(), ICourseContainer {
  
  private val mLessons = arrayListOf<ILessonItem>()
  private val mAffairs = arrayListOf<IAffairItem>()
  
  final override fun addLesson(lesson: ILessonItem): Boolean {
    if (course.addItem(lesson)) {
      mLessons.add(lesson)
      return true
    }
    return false
  }
  
  final override fun addLesson(lessons: List<ILessonItem>) {
    lessons.forEach { addLesson(it) }
  }
  
  final override fun removeLesson(lesson: ILessonItem) {
    course.removeItem(lesson)
    mLessons.remove(lesson)
  }
  
  final override fun clearLesson() {
    mLessons.forEach {
      course.removeItem(it)
    }
    mLessons.clear()
  }
  
  final override fun addAffair(affair: IAffairItem): Boolean {
    if (course.addItem(affair)) {
      mAffairs.add(affair)
      return true
    }
    return false
  }
  
  final override fun addAffair(affairs: List<IAffairItem>) {
    affairs.forEach { addAffair(it) }
  }
  
  final override fun removeAffair(affair: IAffairItem) {
    course.removeItem(affair)
    mAffairs.remove(affair)
  }
  
  final override fun clearAffair() {
    mAffairs.forEach {
      course.removeItem(it)
    }
    mAffairs.clear()
  }
}