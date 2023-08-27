package com.mredrock.cyxbs.course.page.course.item

import android.content.Context
import android.view.View
import com.mredrock.cyxbs.course.page.course.item.base.TouchItemImpl
import com.mredrock.cyxbs.course.page.course.item.view.IOverlapTag
import com.mredrock.cyxbs.lib.course.item.overlap.IOverlapItem

/**
 * 课表 item 的基类
 *
 * 采用与课表一样的设计，每个父类实现一个相关联的功能，减轻以后维护人员的负担
 *
 * ## 注意
 * 点击事件是在 [TouchItemImpl] 中实现的
 *
 * @author 985892345
 * 2023/2/22 12:26
 */
abstract class BaseItem<V> : TouchItemImpl()
  where V : View, V : IOverlapTag // 用于提醒子类需要实现这些接口
{
  abstract override fun createView(context: Context): V
  
  override fun onRefreshOverlap(isAllowAnim: Boolean, hasChanged: Boolean) {
    super.onRefreshOverlap(isAllowAnim, hasChanged)
    refreshShowOverlapTag()
  }
  
  /**
   * 刷新显示 item 右上角重叠标志
   */
  fun refreshShowOverlapTag() {
    val isNeedShowOverlapTag = overlap.hasBelowItem()
    getChildIterable().forEach {
      if (it is IOverlapTag) {
        it.setIsShowOverlapTag(isNeedShowOverlapTag)
      }
    }
  }
  
  override fun compareTo(other: IOverlapItem): Int {
    return if (other is ISingleDayRank) compareToInternal(other) else 1
  }
}