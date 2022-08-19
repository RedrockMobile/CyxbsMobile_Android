package com.mredrock.cyxbs.lib.course.internal.view.course.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.collection.ArrayMap
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.internal.affair.IAffairContainer
import com.mredrock.cyxbs.lib.course.internal.affair.IAffairItem
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.lesson.ILessonContainer
import com.mredrock.cyxbs.lib.course.internal.lesson.ILessonItem
import com.mredrock.cyxbs.lib.course.internal.lesson.period.IAmLessonItem
import com.mredrock.cyxbs.lib.course.internal.lesson.period.INightLessonItem
import com.mredrock.cyxbs.lib.course.internal.lesson.period.IPmLessonItem
import com.mredrock.cyxbs.lib.course.internal.period.RowInclude
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseContainer
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
) : AbstractCourseLayout(context, attrs, defStyleAttr, defStyleRes), ICourseContainer {
  
  private val mOnItemExistListeners = ArrayList<ICourseContainer.OnItemExistListener>(2)
  private val mOnLessonExistListeners = ArrayList<ILessonContainer.OnLessonExistListener>(2)
  private val mOnAffairExistListeners = ArrayList<IAffairContainer.OnAffairExistListener>(2)
  private val mItemByView = ArrayMap<View, IItem>()
  
  final override fun addLessonItem(lesson: ILessonItem) {
    require(lesson is IAmLessonItem || lesson is IPmLessonItem || lesson is INightLessonItem) {
      "未实现该时间段！ ILessonItem = $lesson"
    }
    mOnLessonExistListeners.forEach { it.onLessonAddedBefore(lesson) }
    addItem(lesson)
    mOnLessonExistListeners.forEach { it.onLessonAddedAfter(lesson) }
  }
  
  final override fun addAffairItem(affair: IAffairItem) {
    mOnAffairExistListeners.forEach { it.onAffairAddedBefore(affair) }
    addItem(affair)
    mOnAffairExistListeners.forEach { it.onAffairAddedAfter(affair) }
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
  
  override fun addItemExistListener(l: ICourseContainer.OnItemExistListener) {
    mOnItemExistListeners.add(l)
  }
  
  final override fun addItem(item: IItem) {
    mOnItemExistListeners.forEach { it.onItemAddedBefore(item) }
    super.addItem(item.view, item.lp)
    mItemByView[item.view] = item
    mOnItemExistListeners.forEach { it.onItemAddedAfter(item) }
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
  
  
  
  final override fun isIncludeAmPeriod(item: IItem): RowInclude {
    val sRow = isIncludeAmPeriod(item.startRow)
    val eRow = isIncludeAmPeriod(item.endRow)
    return if (sRow) {
      if (eRow) RowInclude.CONTAIN_ITEM else RowInclude.INTERSECT_TOP
    } else {
      if (eRow) RowInclude.INTERSECT_BOTTOM else RowInclude.CONTAIN_PERIOD
    }
  }
  
  final override fun isIncludeNoonPeriod(item: IItem): RowInclude {
    val sRow = isIncludeNoonPeriod(item.startRow)
    val eRow = isIncludeNoonPeriod(item.endRow)
    return if (sRow) {
      if (eRow) RowInclude.CONTAIN_ITEM else RowInclude.INTERSECT_TOP
    } else {
      if (eRow) RowInclude.INTERSECT_BOTTOM else RowInclude.CONTAIN_PERIOD
    }
  }
  
  final override fun isIncludePmPeriod(item: IItem): RowInclude {
    val sRow = isIncludePmPeriod(item.startRow)
    val eRow = isIncludePmPeriod(item.endRow)
    return if (sRow) {
      if (eRow) RowInclude.CONTAIN_ITEM else RowInclude.INTERSECT_TOP
    } else {
      if (eRow) RowInclude.INTERSECT_BOTTOM else RowInclude.CONTAIN_PERIOD
    }
  }
  
  final override fun isIncludeDuskPeriod(item: IItem): RowInclude {
    val sRow = isIncludeDuskPeriod(item.startRow)
    val eRow = isIncludeDuskPeriod(item.endRow)
    return if (sRow) {
      if (eRow) RowInclude.CONTAIN_ITEM else RowInclude.INTERSECT_TOP
    } else {
      if (eRow) RowInclude.INTERSECT_BOTTOM else RowInclude.CONTAIN_PERIOD
    }
  }
  
  final override fun isIncludeNightPeriod(item: IItem): RowInclude {
    val sRow = isIncludeNightPeriod(item.startRow)
    val eRow = isIncludeNightPeriod(item.endRow)
    return if (sRow) {
      if (eRow) RowInclude.CONTAIN_ITEM else RowInclude.INTERSECT_TOP
    } else {
      if (eRow) RowInclude.INTERSECT_BOTTOM else RowInclude.CONTAIN_PERIOD
    }
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