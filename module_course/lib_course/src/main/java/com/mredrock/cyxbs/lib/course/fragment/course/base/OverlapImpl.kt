package com.mredrock.cyxbs.lib.course.fragment.course.base

import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
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
 * 操控重叠的类
 *
 * ## 特别注意
 * - 该类会拦截使用 addItem() 添加进来的 [IOverlapItem]
 * - 然后在下一个 Runnable 中添加**部分**之前被拦截的 item (如果添加全部会浪费性能)
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
            if (mIsInRefreshOverlapRunnable) {
              // 这里按正常逻辑是下一个 Runnable 中添加进来的
              true
            } else {
              mItemInFreeSet.add(item)
              setOverlap(item)
              tryPostRefreshOverlapRunnable()
              false // IOverlapItem 需要延迟添加进去
            }
          } else true
        }
  
        override fun onItemRemovedBefore(item: IItem, view: View) {
          if (item is IOverlapItem) {
            if (mItemInParentSet.remove(item)) {
              deleteOverlap(item)
              item.overlap.clearOverlap()
              tryPostRefreshOverlapRunnable()
            }
          }
        }
  
        override fun onItemRemovedFail(item: IItem) {
          if (item is IOverlapItem) {
            // 这里说明原操控者需要删除该 item，但可能是因为被上方的拦截而删除失败
            if (mItemInFreeSet.remove(item)) {
              deleteOverlap(item)
              item.overlap.clearOverlap()
              tryPostRefreshOverlapRunnable()
            }
          }
        }
      }
    )
    
    // 设置子 View visibility 改变时的监听
    course.addChildVisibleListener(
      object : OnChildVisibleListener {
        override fun onHideView(parent: ViewGroup, child: View, newVisibility: Int) {
          if (newVisibility == View.INVISIBLE) return // 只监听 GONE 和 VISIBLE
          val item = course.getItemByView(child)
          if (item is IOverlapItem) {
            deleteOverlap(item)
            tryPostRefreshOverlapRunnable()
          }
        }
  
        override fun onShowView(parent: ViewGroup, child: View, oldVisibility: Int) {
          if (oldVisibility == View.INVISIBLE) return // 只监听 GONE 和 VISIBLE
          val item = course.getItemByView(child)
          if (item is IOverlapItem) {
            setOverlap(item)
            tryPostRefreshOverlapRunnable()
          }
        }
      }
    )
  }
  
  private val mItemInFreeSet = hashSetOf<IOverlapItem>()
  private val mItemInParentSet = hashSetOf<IOverlapItem>()
  
  // 是否正处于刷新的 Runnable 中
  private var mIsInRefreshOverlapRunnable = false
  
  // 判断是否重复发送了 Runnable。你只管直接调用 tryPostRefreshOverlapRunnable() 即可，是否为重复调用由该变量判断
  private var _isPostRefreshOverlapRunnable = false
  
  private fun tryPostRefreshOverlapRunnable(): Boolean {
    if (!_isPostRefreshOverlapRunnable) {
      _isPostRefreshOverlapRunnable = true
      course.post {
        _isPostRefreshOverlapRunnable = false
        mIsInRefreshOverlapRunnable = true
        // 先重新询问没有添加进去的 item 是否需要添加进父布局
        val iterator = mItemInFreeSet.iterator()
        while (iterator.hasNext()) {
          val next = iterator.next()
          if (next.overlap.isAddIntoParent()) {
            // 这里又会回调 OnItemExistListener
            if (course.addItem(next)) {
              next.overlap.onAddIntoParentResult(true)
              iterator.remove()
              mItemInParentSet.add(next)
            } else {
              next.overlap.onAddIntoParentResult(false)
            }
          }
        }
        // 遍历所有添加进去的 item 刷新重叠区域
        mItemInParentSet.forEach {
          it.overlap.refreshOverlap()
        }
        mIsInRefreshOverlapRunnable = false
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
          // 如果你想在它们为 0 时删除之前的数据，那么你应该在数据源中删除，而不是在这里删除
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