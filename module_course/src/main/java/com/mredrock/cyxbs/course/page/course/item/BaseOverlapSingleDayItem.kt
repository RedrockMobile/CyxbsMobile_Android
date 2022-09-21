package com.mredrock.cyxbs.course.page.course.item

import android.content.Context
import android.view.View
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.course.page.course.item.view.IOverlapTag
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlapItem
import com.mredrock.cyxbs.lib.course.internal.item.forEachRow
import com.mredrock.cyxbs.lib.course.item.single.AbstractOverlapSingleDayItem
import com.mredrock.cyxbs.lib.course.item.single.ISingleDayData

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:42
 */
abstract class BaseOverlapSingleDayItem<V, D> : AbstractOverlapSingleDayItem(),
  ISingleDayRank,
  ISingleDayData,
  IWeek // 用于整学期界面的周数排序
where V : View, V :IOverlapTag, D : ISingleDayData, D : IWeek // 用于提醒子类需要实现这些接口
{
  
  abstract val data: D
  
  abstract override fun createView(context: Context): V
  
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
      if (it is IOverlapTag) {
        it.setIsShowOverlapTag(isNeedShowOverlapTag)
      }
    }
  }
  
  override fun onClearOverlap() {
    super.onClearOverlap()
    getChildIterable().forEach {
      if (it is IOverlapTag) {
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
  final override val week: Int
    get() = data.week
}