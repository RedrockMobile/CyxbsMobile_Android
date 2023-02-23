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
   *
   * ## 注意
   * - 如果是正在被 [IItemInterceptor] 拦截的 item，则可能返回 null
   */
  fun findItemUnderByXY(x: Int, y: Int): IItem?
  
  /**
   * 寻找对应坐标的 [IItem] 和 View
   *
   * ## 注意
   * - 如果是正在被 [IItemInterceptor] 拦截的 item，则可能返回 null
   */
  fun findPairUnderByXY(x: Int, y: Int): Pair<IItem, View>?
  
  /**
   * 通过自定义筛选器查找 [IItem] 和 View
   *
   * ## 注意
   * - 如果是正在被 [IItemInterceptor] 拦截的 item，则可能返回 null
   */
  fun findPairUnderByFilter(filter: IItem.(View) -> Boolean): Pair<IItem, View>?
  
  /**
   * 添加 item
   *
   * ## 注意
   * - 添加 item 后不一定就会立马添加进父布局中，因为存在 [IItemInterceptor] 会中途拦截
   */
  fun addItem(item: IItem)
  
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
   * 删除监听
   */
  fun removeItemExistListener(l: OnItemExistListener)
  
  /**
   * 添加 [IItem] 和移除 [IItem] 的拦截器
   *
   * 考虑到移除 item 的时候需要回调 [IItemInterceptor.removeItem]，所以没有删除拦截的方法
   */
  fun addItemInterceptor(interceptor: IItemInterceptor)
  
  
  interface OnItemExistListener {
  
    /**
     * 调用 addView() 前回调
     *
     * ## 注意
     * - view 如果被 [IItemInterceptor] 拦截，则为 null
     * - 存在第二次回调中的参数 [item] 是第一次回调中的对象，所以必要时请使用 Set 集合
     */
    fun onItemAddedBefore(item: IItem) {}
  
    /**
     * 调用 addView() 后回调
     *
     * ## 注意
     * - view 如果被 [IItemInterceptor] 拦截，则为 null
     * - 存在第二次回调中的参数 [item] 是第一次回调中的对象，所以必要时请使用 Set 集合
     */
    fun onItemAddedAfter(item: IItem, view: View?) {}
  
    /**
     * 调用 removeView() 前回调，此时可用于设置退场动画
     *
     * ## 注意
     * - view 如果被 [IItemInterceptor] 拦截，则为 null
     * - 存在第二次回调中的参数 [item] 是第一次回调中的对象，所以必要时请使用 Set 集合
     */
    fun onItemRemovedBefore(item: IItem, view: View?) {}
  
    /**
     * 调用 removeView() 后回调
     *
     * ## 注意
     * - view 如果被 [IItemInterceptor] 拦截，则为 null
     * - 存在第二次回调中的参数 [item] 是第一次回调中的对象，所以必要时请使用 Set 集合
     */
    fun onItemRemovedAfter(item: IItem, view: View?) {}
  
  }
  
  interface IItemInterceptor {
  
    /**
     * 拦截 item 的添加
     *
     * 返回 true 后将不再继续遍历后面的 [IItemInterceptor]
     *
     * @return true：进行拦截；false：不进行拦截
     */
    fun addItem(item: IItem): Boolean
  
    /**
     * 移除 item 时的回调
     */
    fun removeItem(item: IItem)
  }
}