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
   * @return 移除是否成功，如果移除失败，说明容器中没有添加该 item，可能是再添加的时候被其他人拦截了
   */
  fun removeItem(item: IItem): Boolean
  
  /**
   * 在已经添加进来的 [view] 中查找 [IItem]
   */
  fun getItemByView(view: View?): IItem?
  
  /**
   * 在已经添加进来的 [item] 中查找 [View]
   */
  fun getViewByItem(item: IItem?): View?
  
  /**
   * 得到 [View] 与 [IItem] 的 [Map]
   */
  fun getItemByViewMap(): Map<View, IItem>
  
  /**
   * 得到 [IItem] 与 [View] 的 [Map]
   */
  fun getViewByItemMap(): Map<IItem, View>
  
  /**
   * 添加 [IItem] 和移除 [IItem] 的监听
   */
  fun addItemExistListener(l: OnItemExistListener)
  
  /**
   * 发送一个 Runnable 延迟到下一个事件序列中删除监听
   */
  fun postRemoveItemExistListener(l: OnItemExistListener)
  
  
  interface OnItemExistListener {
    /**
     * 是否允许添加该 item
     */
    fun isAllowToAddItem(item: IItem): Boolean = true
  
    /**
     * 添加 item 失败时回调
     */
    fun onItemAddedFail(item: IItem) {}
  
    /**
     * 调用 addView() 前回调
     */
    fun onItemAddedBefore(item: IItem, view: View) {}
  
    /**
     * 调用 addView() 后回调
     */
    fun onItemAddedAfter(item: IItem, view: View) {}
  
    /**
     * 调用 removeView() 前回调，此时可用于设置退场动画
     */
    fun onItemRemovedBefore(item: IItem, view: View) {}
  
    /**
     * 调用 removeView() 后回调
     */
    fun onItemRemovedAfter(item: IItem, view: View) {}
  
    /**
     * 移除失败，说明该 item 并没有被添加进容器，有以下两种情况：
     * - 之前添加的时候被 [isAllowToAddItem] 拦截
     * - 之前根本就没有添加过该 item
     */
    fun onItemRemovedFail(item: IItem) {}
  }
}