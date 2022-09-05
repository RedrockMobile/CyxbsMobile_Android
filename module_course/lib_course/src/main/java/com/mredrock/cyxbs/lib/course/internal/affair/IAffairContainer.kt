package com.mredrock.cyxbs.lib.course.internal.affair

import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 13:02
 */
interface IAffairContainer {
  
  /**
   * 添加时候成功，因为可能会被拦截
   */
  fun addAffairItem(affair: IAffairItem): Boolean
  
  fun removeAffairItem(affair: IAffairItem)
  
  /**
   * 添加 [IAffairItem] 和移除 [IAffairItem] 的监听
   *
   * ## 注意
   * 这个是只有调用 [addAffairItem] 方法后才会有回调，如果调用的是 [IItemContainer.addItem] 是不会收到回调的
   */
  fun addAffairExistListener(l: OnAffairExistListener)
  
  /**
   * 寻找对应坐标的 [IAffairItem]
   */
  fun findAffairItemUnderByXY(x: Int, y: Int): IAffairItem?
  
  interface OnAffairExistListener {
    fun isAbleToAddAffair(affair: IAffairItem): Boolean = false
    fun onAffairAddedBefore(affair: IAffairItem) {}
    fun onAffairAddedAfter(affair: IAffairItem) {}
    fun onAffairRemovedBefore(affair: IAffairItem) {}
    fun onAffairRemovedAfter(affair: IAffairItem) {}
  }
}