package com.mredrock.cyxbs.lib.course.item

import android.content.Context
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.item.IOverlapItem
import com.mredrock.cyxbs.lib.course.internal.day.ISingleDayItemData
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.ndhzs.netlayout.attrs.NetLayoutParams
import com.ndhzs.netlayout.view.NetLayout

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/26 17:00
 */
sealed class AbstractOverlapItem(
  private val data: ISingleDayItemData
) : IOverlapItem {
  
  /**
   * 创建子 View，这个 View 才是真正用于显示的
   *
   * 因为存在重叠分开显示的情况，所以需要单独用子 View 来实现（比如：1 2 3 4 节课，中间的 2 3 节课被遮挡了）
   */
  protected abstract fun createView(context: Context, startNode: Int, length: Int): View
  
  /**
   * 需要清除重叠区域时的回调
   */
  protected open fun onClearOverlap() {}
  
  /**
   * 即将被添加进课表时的回调
   */
  protected open fun onAddIntoCourse() {}
  
  /**
   * 创建子 View 的 [NetLayoutParams]
   */
  protected open fun createNetLayoutParams(
    startRow: Int,
    endRow: Int
  ): NetLayoutParams {
    return NetLayoutParams(startRow, endRow, 0, 0).apply {
      leftMargin = 1.5F.dp2px
      rightMargin = leftMargin
      topMargin = leftMargin
      bottomMargin = leftMargin
    }
  }
  
  /**
   * 遍历自身
   */
  inline fun forEachPosition(block: (position: Int) -> Unit) {
    for (position in 0 until length) block.invoke(position)
  }
  
  /**
   * 用于实现折叠的 View
   *
   * [createView] 中得到的 View 都会添加进这个 [view] 中
   */
  final override lateinit var view: NetLayout
    private set
  
  final override val weekNum: Int
    get() = data.weekNum
  
  final override val startNode: Int
    get() = data.startNode
  
  final override val length: Int
    get() = data.length
  
  private val showNode: SparseBooleanArray = SparseBooleanArray()
  private val aboveItemByShowNode = SparseArray<IOverlapItem>()
  private val belowItemBySHowNode = SparseArray<IOverlapItem>()
  
  final override fun isDisplayable(position: Int): Boolean {
    return showNode.get(position, true)
  }
  
  final override fun onOverlapped(position: Int, item: IOverlapItem?) {
    showNode.put(position, false)
    aboveItemByShowNode.put(position, item)
  }
  
  final override fun onOverlapping(position: Int, item: IOverlapItem?) {
    showNode.put(position, true)
    belowItemBySHowNode.put(position, item)
  }
  
  final override fun clearOverlap() {
    aboveItemByShowNode.clear()
    belowItemBySHowNode.clear()
    showNode.clear()
    onClearOverlap()
  }
  
  final override fun isAllowToAddIntoCourse(context: Context): Boolean {
    var isShow = false
    var s = startRow
    var e = s - 1
    // 实现重叠后分开显示的逻辑
    forEachPosition { position ->
      if (isDisplayable(position)) {
        if (!isShow) {
          isShow = true
          view = NetLayout(context).apply { setRowColumnCount(length, 1) }
        }
        e = startRow + position
      } else {
        if (s <= e) {
          view.addView(
            createView(context, s, e - s + 1),
            createNetLayoutParams(s - startRow, e - startRow)
          )
        }
        s = startRow + position + 1
      }
    }
    // 判断 e 是不是最后一格，如果是的话，就要单独加上
    if (e == endRow) {
      view.addView(
        createView(context, s, e - s + 1),
        createNetLayoutParams(s - startRow, e - startRow)
      )
    }
    if (isShow) onAddIntoCourse()
    return isShow
  }
  
  final override fun getAboveItem(position: Int): IOverlapItem? {
    return aboveItemByShowNode.get(position)
  }
  
  final override fun getBelowItem(position: Int): IOverlapItem? {
    return belowItemBySHowNode.get(position)
  }
}