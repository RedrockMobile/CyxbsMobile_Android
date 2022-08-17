package com.mredrock.cyxbs.lib.course.view.course.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.affair.IAffairContainer
import com.mredrock.cyxbs.lib.course.affair.IAffairItem
import com.mredrock.cyxbs.lib.course.item.IItem
import com.mredrock.cyxbs.lib.course.lesson.ILesson
import com.mredrock.cyxbs.lib.course.lesson.period.IAmLesson
import com.mredrock.cyxbs.lib.course.lesson.period.INightLesson
import com.mredrock.cyxbs.lib.course.lesson.period.IPmLesson
import com.mredrock.cyxbs.lib.course.period.RowInclude
import com.mredrock.cyxbs.lib.course.period.am.IAmItem
import com.mredrock.cyxbs.lib.course.period.dusk.IDuskItem
import com.mredrock.cyxbs.lib.course.period.night.INightItem
import com.mredrock.cyxbs.lib.course.period.noon.INoonItem
import com.mredrock.cyxbs.lib.course.period.pm.IPmItem
import com.mredrock.cyxbs.lib.course.view.ICourseContainer
import com.ndhzs.netlayout.attrs.NetLayoutParams

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
) : AbstractCourseLayout(context, attrs, defStyleAttr, defStyleRes), ICourseContainer, IAffairContainer {
  
  private val mOnItemAddedListeners = ArrayList<ICourseContainer.OnItemAddedListener>(2)
  
  final override fun addLesson(lesson: ILesson) {
    when (lesson) {
      is IAmLesson -> addAmItem(lesson)
      is IPmLesson -> addPmItem(lesson)
      is INightLesson -> addNightItem(lesson)
      else -> error("未实现该时间段！")
    }
  }
  
  final override fun addAmItem(item: IAmItem) = addItem(item)
  final override fun addNoonItem(item: INoonItem) = addItem(item)
  final override fun addPmItem(item: IPmItem) = addItem(item)
  final override fun addDuskItem(item: IDuskItem) = addItem(item)
  final override fun addNightItem(item: INightItem) = addItem(item)
  final override fun addAffairItem(item: IAffairItem) = addItem(item)
  
  final override fun addItemAddedListener(l: ICourseContainer.OnItemAddedListener) {
    mOnItemAddedListeners.add(l)
  }
  
  private fun addItem(item: IItem) {
    mOnItemAddedListeners.forEach { it.addItemBefore(item) }
    super.addItem(item.view, item.lp)
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
  
  
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.ERROR)
  final override fun addItem(item: View, lp: NetLayoutParams) {
    super.addItem(item, lp)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.ERROR)
  final override fun addView(child: View?) {
    super.addView(child)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.ERROR)
  final override fun addView(child: View?, index: Int) {
    super.addView(child, index)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.ERROR)
  final override fun addView(child: View?, width: Int, height: Int) {
    super.addView(child, width, height)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.ERROR)
  final override fun addView(child: View?, params: LayoutParams?) {
    super.addView(child, params)
  }
}