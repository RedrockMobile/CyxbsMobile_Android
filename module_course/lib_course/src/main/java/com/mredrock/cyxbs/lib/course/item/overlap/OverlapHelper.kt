package com.mredrock.cyxbs.lib.course.item.overlap

import android.util.SparseArray
import androidx.core.util.forEach
import androidx.core.util.isNotEmpty
import com.mredrock.cyxbs.lib.course.utils.getOrPut

/**
 * 重叠功能的帮助类，实现了一些模板代码
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/9 19:45
 */
class OverlapHelper(
  private val logic: IOverlapLogic
) : IOverlap {
  
  private val mAboveItemByRowColumn = SparseArray<SparseArray<IOverlapItem>>()
  private val mBelowItemByRowColumn = SparseArray<SparseArray<IOverlapItem>>()
  
  // 刷新动画上锁次数
  private var mRefreshAnimLockCount = 0
  
  override fun isAddIntoParent() = logic.isAddIntoParent()
  
  override fun refreshOverlap(isAllowAnim: Boolean) {
    logic.refreshOverlap(mRefreshAnimLockCount == 0 && isAllowAnim)
  }
  
  override fun clearOverlap() {
    mAboveItemByRowColumn.clear()
    mBelowItemByRowColumn.clear()
    logic.onClearOverlap()
  }
  
  override fun isDisplayable(row: Int, column: Int): Boolean {
    return getAboveItem(row, column) == null
  }
  
  override fun onAboveItem(row: Int, column: Int, item: IOverlapItem?) {
    mAboveItemByRowColumn.getOrPut(row) { SparseArray() }
      .put(column, item)
  }
  
  override fun onBelowItem(row: Int, column: Int, item: IOverlapItem?) {
    mBelowItemByRowColumn.getOrPut(row) { SparseArray() }
      .put(column, item)
  }
  
  override fun getAboveItem(row: Int, column: Int): IOverlapItem? {
    return mAboveItemByRowColumn.get(row)?.get(column)
  }
  
  override fun getAboveItems(): List<IOverlapItem> {
    val list = arrayListOf<IOverlapItem>()
    mAboveItemByRowColumn.forEach { _, array ->
      array.forEach { _, item ->
        list.add(item)
      }
    }
    return list
  }
  
  override fun hasAboveItem(): Boolean {
    mAboveItemByRowColumn.forEach { _, array ->
      if (array.isNotEmpty()) return true
    }
    return false
  }
  
  override fun getBelowItem(row: Int, column: Int): IOverlapItem? {
    return mBelowItemByRowColumn.get(row)?.get(column)
  }
  
  override fun getBelowItems(): List<IOverlapItem> {
    val list = arrayListOf<IOverlapItem>()
    mBelowItemByRowColumn.forEach { _, array ->
      array.forEach { _, item ->
        list.add(item)
      }
    }
    return list
  }
  
  override fun hasBelowItem(): Boolean {
    mBelowItemByRowColumn.forEach { _, array ->
      if (array.isNotEmpty()) return true
    }
    return false
  }
  
  override fun lockRefreshAnim() {
    mRefreshAnimLockCount++
  }
  
  override fun unlockRefreshAnim() {
    mRefreshAnimLockCount--
  }
  
  interface IOverlapLogic {
  
    /**
     * [IOverlap.isAddIntoParent]
     */
    fun isAddIntoParent(): Boolean
  
    /**
     * [IOverlap.refreshOverlap]
     */
    fun refreshOverlap(isAllowAnim: Boolean)
  
    /**
     * 需要清除重叠区域时的回调
     */
    fun onClearOverlap()
  }
}