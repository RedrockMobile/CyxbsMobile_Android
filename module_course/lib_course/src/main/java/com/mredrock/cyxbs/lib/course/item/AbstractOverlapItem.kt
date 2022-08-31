package com.mredrock.cyxbs.lib.course.item

import android.content.Context
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.item.IOverlapItem
import com.mredrock.cyxbs.lib.course.internal.day.ISingleDayItemData
import com.mredrock.cyxbs.lib.course.internal.view.course.lp.ItemLayoutParams
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
  
  abstract fun createView(context: Context, startNode: Int, length: Int): View
  abstract override val lp: ItemLayoutParams
  
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
  
  final override val weekNum: Int
    get() = data.weekNum
  
  final override val startNode: Int
    get() = data.startNode
  
  final override val length: Int
    get() = data.length
  
  private val showNode: SparseBooleanArray = SparseBooleanArray()
  private val aboveItemByShowNode = SparseArray<IOverlapItem>()
  private val belowItemBySHowNode = SparseArray<IOverlapItem>()
  
  final override fun isShow(position: Int): Boolean {
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
  
  /**
   * 需要清除重叠区域时的回调
   */
  protected open fun onClearOverlap() {}
  
  final override fun isAllowToAddIntoCourse(context: Context): Boolean {
    var isShow = false
    var s = startRow
    var e = s - 1
    // 实现重叠后分开显示的逻辑
    forEachPosition { position ->
      if (isShow(position)) {
        if (!isShow) {
          isShow = true
          view = NetLayout(context).apply { setRowColumnCount(length, 1) }
        }
        e = startRow + position
      } else {
        if (s <= e) {
          view.addView(
            createView(context, s, e - s + 1),
            NetLayoutParams(s - startRow, e - startRow, 0, 0)
              .apply {
                leftMargin = 1.5F.dp2px
                rightMargin = leftMargin
                topMargin = leftMargin
                bottomMargin = leftMargin
              }
          )
        }
        s = startRow + position + 1
      }
    }
    // 判断 e 是不是最后一格，如果是的话，就要单独加上
    if (e == endRow) {
      view.addView(
        createView(context, s, e - s + 1),
        NetLayoutParams(s - startRow, e - startRow, 0, 0)
          .apply {
            leftMargin = 1.5F.dp2px
            rightMargin = leftMargin
            topMargin = leftMargin
            bottomMargin = leftMargin
          }
      )
    }
    if (isShow) onAddIntoCourse()
    return isShow
  }
  
  /**
   * 即将被添加进课表时的回调
   */
  open fun onAddIntoCourse() {}
  
  final override fun getAboveItem(position: Int): IOverlapItem? {
    return aboveItemByShowNode.get(position)
  }
  
  final override fun getBelowItem(position: Int): IOverlapItem? {
    return belowItemBySHowNode.get(position)
  }
}