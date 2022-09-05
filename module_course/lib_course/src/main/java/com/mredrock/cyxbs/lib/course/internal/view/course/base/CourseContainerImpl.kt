package com.mredrock.cyxbs.lib.course.internal.view.course.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.collection.ArrayMap
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.internal.affair.IAffairContainer
import com.mredrock.cyxbs.lib.course.internal.affair.IAffairItem
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer
import com.mredrock.cyxbs.lib.course.internal.lesson.ILessonContainer
import com.mredrock.cyxbs.lib.course.internal.lesson.ILessonItem
import com.ndhzs.netlayout.attrs.NetLayoutParams
import com.ndhzs.netlayout.child.IChildExistListener

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 0:23
 */
@Suppress("DeprecatedCallableAddReplaceWith")
abstract class CourseContainerImpl @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = R.attr.courseLayoutStyle,
  defStyleRes: Int = 0
) : AbstractCourseViewGroup(context, attrs, defStyleAttr, defStyleRes),
  IItemContainer, ILessonContainer, IAffairContainer
{
  
  private val mOnItemExistListeners = ArrayList<IItemContainer.OnItemExistListener>(2)
  private val mOnLessonExistListeners = ArrayList<ILessonContainer.OnLessonExistListener>(2)
  private val mOnAffairExistListeners = ArrayList<IAffairContainer.OnAffairExistListener>(2)
  private val mItemByView = ArrayMap<View, IItem>()
  
  final override fun addLessonItem(lesson: ILessonItem): Boolean {
    if (mOnLessonExistListeners.all { it.isAllowToAddLesson(lesson) }) {
      mOnLessonExistListeners.forEach { it.onLessonAddedBefore(lesson) }
      if (addItem(lesson)) {
        mOnLessonExistListeners.forEach { it.onLessonAddedAfter(lesson) }
        return true
      }
    }
    return false
  }
  
  final override fun addAffairItem(affair: IAffairItem): Boolean {
    if (mOnAffairExistListeners.all { it.isAbleToAddAffair(affair) }) {
      mOnAffairExistListeners.forEach { it.onAffairAddedBefore(affair) }
      if (addItem(affair)) {
        mOnAffairExistListeners.forEach { it.onAffairAddedAfter(affair) }
        return true
      }
    }
    return false
  }
  
  override fun removeLessonItem(lesson: ILessonItem) {
    if (mItemByView.contains(lesson.view)) {
      mOnLessonExistListeners.forEach { it.onLessonRemovedBefore(lesson) }
      removeItem(lesson)
      mOnLessonExistListeners.forEach { it.onLessonRemovedAfter(lesson) }
    }
  }
  
  override fun removeAffairItem(affair: IAffairItem) {
    if (mItemByView.contains(affair.view)) {
      mOnAffairExistListeners.forEach { it.onAffairRemovedBefore(affair) }
      removeItem(affair)
      mOnAffairExistListeners.forEach { it.onAffairRemovedAfter(affair) }
    }
  }
  
  final override fun addLessonExistListener(l: ILessonContainer.OnLessonExistListener) {
    mOnLessonExistListeners.add(l)
  }
  
  final override fun addAffairExistListener(l: IAffairContainer.OnAffairExistListener) {
    mOnAffairExistListeners.add(l)
  }
  
  override fun addItemExistListener(l: IItemContainer.OnItemExistListener) {
    mOnItemExistListeners.add(l)
  }
  
  final override fun addItem(item: IItem): Boolean {
    if (mOnItemExistListeners.all { it.isAllowToAddItem(item) }) {
      mOnItemExistListeners.forEach { it.onItemAddedBefore(item) }
      super.addItem(item.view, item.lp)
      mItemByView[item.view] = item
      mOnItemExistListeners.forEach { it.onItemAddedAfter(item) }
      return true
    }
    return false
  }
  
  final override fun removeItem(item: IItem) {
    if (mItemByView.contains(item.view)) {
      mOnItemExistListeners.forEach { it.onItemRemovedBefore(item) }
      removeView(item.view)
      mItemByView.remove(item.view)
      mOnItemExistListeners.forEach { it.onItemRemovedAfter(item) }
    }
  }
  
  
  
  override fun findLessonItemUnderByXY(x: Int, y: Int): ILessonItem? {
    return findItemUnderByXY(x, y) as? ILessonItem
  }
  
  override fun findAffairItemUnderByXY(x: Int, y: Int): IAffairItem? {
    return findItemUnderByXY(x, y) as? IAffairItem
  }
  
  override fun findItemUnderByXY(x: Int, y: Int): IItem? {
    return findViewUnderByXY(x, y)?.let { mItemByView[it] }
  }
  
  
  init {
    addChildExistListener(
      object : IChildExistListener {
        override fun onChildViewAdded(parent: View, child: View) {}
        override fun onChildViewRemoved(parent: View, child: View) {
          // 当 view 直接被你移除时清除掉引用
          mItemByView.remove(child)
        }
      }
    )
  }
  
  
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.HIDDEN)
  final override fun addItem(item: View, lp: NetLayoutParams) {
    super.addItem(item, lp)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.HIDDEN)
  final override fun addView(child: View?) {
    super.addView(child)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.HIDDEN)
  final override fun addView(child: View?, index: Int) {
    super.addView(child, index)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.HIDDEN)
  final override fun addView(child: View?, width: Int, height: Int) {
    super.addView(child, width, height)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.HIDDEN)
  final override fun addView(child: View?, params: LayoutParams?) {
    super.addView(child, params)
  }
}