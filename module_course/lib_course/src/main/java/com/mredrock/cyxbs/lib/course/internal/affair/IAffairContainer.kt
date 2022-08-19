package com.mredrock.cyxbs.lib.course.internal.affair

import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseContainer

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 13:02
 */
interface IAffairContainer {
  
  fun addAffairItem(affair: IAffairItem)
  
  fun removeAffairItem(affair: IAffairItem)
  
  /**
   * 得到中午时间段开始时的高度值
   */
  fun getNoonStartHeight(): Int
  
  /**
   * 得到中午时间段结束时的高度值
   */
  fun getNoonEndHeight(): Int
  
  /**
   * 得到傍晚时间段开始时的高度值
   */
  fun getDuskStartHeight(): Int
  
  /**
   * 得到傍晚时间段结束时的高度值
   */
  fun getDuskEndHeight(): Int
  
  /**
   * 添加 [IAffairItem] 和移除 [IAffairItem] 的监听
   *
   * ## 注意
   * 这个是只有调用 [addAffairItem] 方法后才会有回调，如果调用的是 [ICourseContainer.addItem] 是不会收到回调的
   */
  fun addAffairExistListener(l: OnAffairExistListener)
  
  /**
   * 寻找对应坐标的 [IAffairItem]
   */
  fun findAffairItemUnderByXY(x: Int, y: Int): IAffairItem?
  
  interface OnAffairExistListener {
    fun onAffairAddedBefore(affair: IAffairItem) {}
    fun onAffairAddedAfter(affair: IAffairItem) {}
    fun onAffairRemovedBefore(affair: IAffairItem) {}
    fun onAffairRemovedAfter(affair: IAffairItem) {}
  }
}