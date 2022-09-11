package com.mredrock.cyxbs.lib.course.fragment.course.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.course.fragment.course.expose.container.ICourseContainer
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer
import com.mredrock.cyxbs.lib.course.item.affair.IAffairItem
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem
import com.mredrock.cyxbs.lib.course.utils.forEachInline

/**
 * 掌控 [ILessonItem] 和 [IAffairItem] 的容器
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 15:36
 */
abstract class ContainerImpl : AbstractCourseBaseFragment(), ICourseContainer {
  
  private val mLessons = hashSetOf<ILessonItem>()
  private val mAffairs = hashSetOf<IAffairItem>()
  
  final override fun addLesson(lesson: ILessonItem) {
    course.addItem(lesson)
    mLessons.add(lesson)
  }
  
  final override fun addLesson(lessons: List<ILessonItem>) {
    lessons.forEachInline { addLesson(it) }
  }
  
  final override fun removeLesson(lesson: ILessonItem) {
    course.removeItem(lesson)
    mLessons.remove(lesson)
  }
  
  final override fun clearLesson() {
    // 因为后面的监听中会调用 mLessons.remove()，所以这里使用迭代先删除
    val iterator = mLessons.iterator()
    while (iterator.hasNext()) {
      val next = iterator.next()
      iterator.remove() // 先删除
      course.removeItem(next)
    }
    mLessons.clear()
  }
  
  final override fun getLessonsSize(): Int {
    return mLessons.size
  }
  
  final override fun containLesson(lesson: ILessonItem?): Boolean {
    return mLessons.contains(lesson)
  }
  
  override fun getLessonIterable(): Set<ILessonItem> {
    return mLessons
  }
  
  
  
  final override fun addAffair(affair: IAffairItem) {
    course.addItem(affair)
    mAffairs.add(affair)
  }
  
  final override fun addAffair(affairs: List<IAffairItem>) {
    affairs.forEachInline { addAffair(it) }
  }
  
  final override fun removeAffair(affair: IAffairItem) {
    course.removeItem(affair)
    mAffairs.remove(affair)
  }
  
  final override fun clearAffair() {
    // 因为后面的监听中会调用 mLessons.remove()，所以这里使用迭代先删除
    val iterator = mAffairs.iterator()
    while (iterator.hasNext()) {
      val next = iterator.next()
      iterator.remove() // 先删除
      course.removeItem(next)
    }
    mAffairs.clear()
  }
  
  final override fun getAffairsSize(): Int {
    return mAffairs.size
  }
  
  final override fun containAffair(affair: IAffairItem?): Boolean {
    return mAffairs.contains(affair)
  }
  
  override fun getAffairIterable(): Set<IAffairItem> {
    return mAffairs
  }
  
  
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    // 可能存在使用其他方法添加进来的，所以需要监听一下
    course.addItemExistListener(
      object : IItemContainer.OnItemExistListener {
        override fun onItemAddedFail(item: IItem) {
          // 被拦截了也需要添加进去
          when (item) {
            is ILessonItem -> mLessons.add(item)
            is IAffairItem -> mAffairs.add(item)
          }
        }
        
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