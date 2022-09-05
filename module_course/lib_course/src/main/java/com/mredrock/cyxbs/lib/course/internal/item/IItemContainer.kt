package com.mredrock.cyxbs.lib.course.internal.item

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
  fun findItemUnderByXY(x: Int, y: Int): IItem?
  
  /**
   * 添加时候成功，因为可能会被拦截
   */
  fun addItem(item: IItem): Boolean
  
  fun removeItem(item: IItem)
  
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