package com.mredrock.cyxbs.lib.course.internal.item

import android.view.View

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 16:08
 */
interface IItemContainer {
  /**
   * 寻找对应坐标的 [IItem]
   */
  fun findPairUnderByXY(x: Int, y: Int): Pair<IItem, View>?
  
  /**
   * 通过自定义筛选器查找 [IItem]
   */
  fun findPairUnderByFilter(filter: IItem.(View) -> Boolean): Pair<IItem, View>?
  
  /**
   * @return 添加是否成功，因为可能会被拦截
   */
  fun addItem(item: IItem): Boolean
  
  /**
   * 移除 [IItem]
   */
  fun removeItem(item: IItem)
  
  /**
   * 在已经添加进来的 [view] 中查找 [IItem]
   */
  fun getItemByView(view: View?): IItem?
  
  /**
   * 在已经添加进来的 [item] 中查找 [View]
   */
  fun getViewByItem(item: IItem?): View?
  
  /**
   * 添加 [IItem] 和移除 [IItem] 的监听
   */
  fun addItemExistListener(l: OnItemExistListener)
  
  interface OnItemExistListener {
    fun isAllowToAddItem(item: IItem): Boolean = true
    fun onItemAddedBefore(item: IItem) {}
    fun onItemAddedAfter(item: IItem) {}
    fun onItemRemovedBefore(item: IItem) {}
    fun onItemRemovedAfter(item: IItem) {}
  }
}