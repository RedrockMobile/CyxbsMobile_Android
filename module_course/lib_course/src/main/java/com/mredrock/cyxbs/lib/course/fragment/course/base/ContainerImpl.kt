package com.mredrock.cyxbs.lib.course.fragment.course.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.course.fragment.course.expose.container.ICourseContainer
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer
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
  
  final override fun addLesson(lesson: ILessonItem) {
    course.addItem(lesson)
  }
  
  final override fun addLesson(lessons: List<ILessonItem>) {
    lessons.forEach { addLesson(it) }
  }
  
  final override fun removeLesson(lesson: ILessonItem) {
    course.removeItem(lesson)
  }
  
  final override fun clearLesson() {
    // 因为后面的监听中会调用 mLessons.remove()，迭代中是不能删除的
    mLessons.toList().forEach {
      course.removeItem(it)
    }
    mLessons.clear()
  }
  
  final override fun addAffair(affair: IAffairItem) {
    course.addItem(affair)
  }
  
  final override fun addAffair(affairs: List<IAffairItem>) {
    affairs.forEach { addAffair(it) }
  }
  
  final override fun removeAffair(affair: IAffairItem) {
    course.removeItem(affair)
  }
  
  final override fun clearAffair() {
    // 因为后面的监听中会调用 mAffairs.remove()，迭代中是不能删除的
    mAffairs.toList().forEach {
      course.removeItem(it)
    }
    mAffairs.clear()
  }
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    course.addItemExistListener(
      object : IItemContainer.OnItemExistListener {
        override fun onItemAddedAfter(item: IItem) {
          when (item) {
            is ILessonItem -> mLessons.add(item)
            is IAffairItem -> mAffairs.add(item)
          }
        }
  
        override fun onItemRemovedAfter(item: IItem) {
          when (item) {
            is ILessonItem -> mLessons.remove(item)
            is IAffairItem -> mAffairs.remove(item)
          }
        }
      }
    )
  }
}