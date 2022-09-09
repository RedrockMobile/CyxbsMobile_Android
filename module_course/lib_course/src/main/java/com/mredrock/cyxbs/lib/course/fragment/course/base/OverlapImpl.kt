package com.mredrock.cyxbs.lib.course.fragment.course.base

import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.collection.arraySetOf
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlapContainer
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlapItem
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer
import com.mredrock.cyxbs.lib.course.internal.item.forEachColumn
import com.mredrock.cyxbs.lib.course.internal.item.forEachRow
import com.mredrock.cyxbs.lib.course.utils.getOrPut
import com.ndhzs.netlayout.transition.OnChildVisibleListener
import java.util.*

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/8 12:19
 */
abstract class OverlapImpl : FoldImpl(), IOverlapContainer {
  
  override fun compareOverlayItem(row: Int, column: Int, o1: IOverlapItem, o2: IOverlapItem): Int {
    return o1.compareTo(o2)
  }
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    // 观察子 View 的添加，筛选出 IOverlapItem
    course.addItemExistListener(
      object : IItemContainer.OnItemExistListener {
        override fun isAllowToAddItem(item: IItem): Boolean {
          return if (item is IOverlapItem) {
            if (course.getViewByItem(item) == null) {
              // 判断是否是重复添加，重复添加会直接报错
              if (mItemFreeSet.contains(item)) {
                // 这里按正常逻辑是下一个 Runnable 中添加进来的
                true
              } else {
                mItemFreeSet.add(item)
                setOverlap(item)
                tryPostRefreshOverlapRunnable()
                false // IOverlapItem 需要延迟添加进去
              }
            } else false
          } else true
        }
  
        override fun onItemRemovedBefore(item: IItem) {
          if (item is IOverlapItem) {
            // 因为考虑到有些 item 可能会根据底下的 item 而改变，所以就直接全部刷新算了
            deleteOverlap(item)
            tryPostRefreshOverlapRunnable()
            item.overlap.clearOverlap()
            mItemFreeSet.remove(item)
            mItemInParentSet.remove(item)
          }
        }
      }
    )
    
    // 设置子 View visibility 改变时的监听
    course.addChildVisibleListener(
      object : OnChildVisibleListener {
        override fun onHideView(parent: ViewGroup, child: View, newVisibility: Int) {
          val item = course.getItemByView(child)
          if (item is IOverlapItem) {
            deleteOverlap(item)
            tryPostRefreshOverlapRunnable()
          }
        }
  
        override fun onShowView(parent: ViewGroup, child: View, oldVisibility: Int) {
          val item = course.getItemByView(child)
          if (item is IOverlapItem) {
            setOverlap(item)
            tryPostRefreshOverlapRunnable()
          }
        }
      }
    )
  }
  
  private val mItemFreeSet = arraySetOf<IOverlapItem>()
  private val mItemInParentSet = arraySetOf<IOverlapItem>()
  
  private var _isInRefreshing = false
  
  private fun tryPostRefreshOverlapRunnable(): Boolean {
    if (!_isInRefreshing) {
      _isInRefreshing = true
      course.post {
        // 先重新询问没有添加进去的 item 是否需要添加进父布局
        val iterator = mItemFreeSet.iterator()
        while (iterator.hasNext()) {
          val next = iterator.next()
          if (next.overlap.isAddIntoParent()) {
            // 这里又会回调 OnItemExistListener
            if (course.addItem(next)) {
              iterator.remove()
              mItemInParentSet.add(next)
            }
          }
        }
        // 遍历所有添加进去的 item 刷新重叠区域
        mItemInParentSet.forEach {
          it.overlap.refreshOverlap()
        }
        _isInRefreshing = false
      }
      return true
    }
    return false
  }
  
  
  
  /**
   * 设置重叠，不一定是在 item 被添加进来时调用
   */
  private fun setOverlap(item: IOverlapItem) {
    item.lp.forEachRow { row ->
      item.lp.forEachColumn { column ->
        getGrid(row, column).setOverlap(item)
      }
    }
  }
  
  /**
   * 删除重叠
   */
  private fun deleteOverlap(item: IOverlapItem) {
    item.lp.forEachRow { row ->
      item.lp.forEachColumn { column ->
        getGrid(row, column).deleteOverlap(item)
      }
    }
  }
  
  private val mRowColumnMap = SparseArray<SparseArray<Grid>>()
  
  private fun getGrid(row: Int, column: Int): Grid {
    return mRowColumnMap.getOrPut(row) { SparseArray() }
      .getOrPut(column) { Grid(row, column) }
  }
  
  /**
   * 管理每个表格的工具类
   */
  private inner class Grid(val row: Int, val column: Int) {
    
    /**
     * TreeSet 该数据结构有个特点就是如果 Compare 比较出来等于 0，则会把之前那个给覆盖掉
     */
    private val sort = TreeSet<IOverlapItem> { o1, o2 ->
      compareOverlayItem(row, column, o1, o2).also {
        if (it == 0 && o1 !== o2) {
          throw RuntimeException("不允许在对象不相等时返回 0 !")
        }
      }
    }
  
    /**
     * 设置重叠
     */
    fun setOverlap(item: IOverlapItem) {
      sort.add(item)
      val higher = sort.higher(item)
      higher?.overlap?.onBelowItem(row, column, item)
      item.overlap.onAboveItem(row, column, higher)
      
      val lower = sort.lower(item)
      item.overlap.onBelowItem(row, column, lower)
      lower?.overlap?.onAboveItem(row, column, item)
    }
    
    /**
     * 删除重叠
     *
     * 注意：这个 remove 必须要满足 Compare 比较等于 0 时才能被 remove 掉
     */
    fun deleteOverlap(item: IOverlapItem) {
      if (sort.contains(item)) {
        val higher = sort.higher(item)
        val lower = sort.lower(item)
        higher?.overlap?.onBelowItem(row, column, lower)
        lower?.overlap?.onAboveItem(row, column, higher)
        sort.remove(item)
      }
    }
  }
}