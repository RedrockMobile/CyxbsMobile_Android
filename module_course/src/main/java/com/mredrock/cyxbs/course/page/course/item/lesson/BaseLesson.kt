package com.mredrock.cyxbs.course.page.course.item.lesson

import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.course.page.course.item.ISingleDayRank
import com.mredrock.cyxbs.course.page.course.item.view.ItemView
import com.mredrock.cyxbs.course.page.course.utils.container.base.IDataOwner
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlapItem
import com.mredrock.cyxbs.lib.course.internal.item.forEachRow
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem
import com.mredrock.cyxbs.lib.course.item.single.AbstractOverlapSingleDayItem
import com.mredrock.cyxbs.lib.course.item.single.ISingleDayData

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:42
 */
sealed class BaseLesson : AbstractOverlapSingleDayItem(),
  ISingleDayRank,
  ISingleDayData,
  ILessonItem,
  IWeek,
  IDataOwner<StuLessonData>
{
  override fun compareTo(other: IOverlapItem): Int {
    return if (other is ISingleDayRank) compareToInternal(other) else 1
  }
  
  override fun onRefreshOverlap() {
    super.onRefreshOverlap()
    var isNeedShowOverlapTag = false
    lp.forEachRow { row ->
      if (overlap.getBelowItem(row, lp.weekNum) != null) {
        isNeedShowOverlapTag = true
        return@forEachRow
      }
    }
    getChildIterable().forEach {
      if (it is ItemView) {
        it.setIsShowOverlapTag(isNeedShowOverlapTag)
      }
    }
  }
  
  override fun onClearOverlap() {
    super.onClearOverlap()
    getChildIterable().forEach {
      if (it is ItemView) {
        it.setIsShowOverlapTag(false)
      }
    }
  }
  
  final override val weekNum: Int
    get() = data.weekNum
  final override val startNode: Int
    get() = data.startNode
  final override val length: Int
    get() = data.length
}