package com.mredrock.cyxbs.lib.course.item

import android.content.Context
import android.util.SparseArray
import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.item.IOverlapItem
import com.mredrock.cyxbs.lib.course.internal.day.ISingleDayItemData
import com.mredrock.cyxbs.lib.course.internal.item.forEachRow
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
  protected abstract fun createView(context: Context, parentStartRow: Int, parentEndRow: Int): View
  
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
    parentStartRow: Int,
    parentEndRow: Int
  ): NetLayoutParams {
    return NetLayoutParams(
      parentStartRow - startRow,
      parentEndRow - startRow,
      0, 0
    ).apply {
      leftMargin = 1.2F.dp2px
      rightMargin = leftMargin
      topMargin = leftMargin
      bottomMargin = leftMargin
    }
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
  
  private val mAboveItemByRow = SparseArray<IOverlapItem>()
  private val mBelowItemByRow = SparseArray<IOverlapItem>()
  
  final override fun isDisplayable(row: Int): Boolean {
    return getAboveItem(row) == null
  }
  
  final override fun onAboveItem(row: Int, item: IOverlapItem?) {
    mAboveItemByRow.put(row, item)
  }
  
  final override fun onBelowItem(row: Int, item: IOverlapItem?) {
    mBelowItemByRow.put(row, item)
  }
  
  final override fun clearOverlap() {
    mAboveItemByRow.clear()
    mBelowItemByRow.clear()
    onClearOverlap()
  }
  
  final override fun isAllowToAddIntoCourse(context: Context): Boolean {
    var isShow = false
    var s = startRow
    var e = s - 1
    // 实现重叠后分开显示的逻辑
    forEachRow { row ->
      if (isDisplayable(row)) {
        if (!isShow) {
          isShow = true
          view = NetLayout(context).apply { setRowColumnCount(length, 1) }
        }
        e = row
      } else {
        if (s <= e) {
          view.addView(
            createView(context, s, e),
            createNetLayoutParams(s, e)
          )
        }
        s = row + 1
      }
    }
    // 判断 e 是不是最后一格，如果是的话，就要单独加上
    if (e == endRow) {
      view.addView(
        createView(context, s, e),
        createNetLayoutParams(s, e)
      )
    }
    if (!isShow) {
      forEachRow { row ->
        val above = getAboveItem(row)
        if (above != null) {
          if (above.getAboveItem(row) == null) {
            // 如果存在某一个位置处于第二层，则允许显示
            // 允许显示的原因是：点击顶上的 View 有一个 Q 弹动画，可以显示下面的 View
            var aboveShowBottomRow = above.endRow
            for (r in (row + 1) .. above.endRow) {
              if (above.getAboveItem(r) != null) {
                // 寻找到 above 从 row 开始往后的第一个有重叠的位置，这个位置到 row 的区域才可以用来显示下面的 View
                // 如果超过这个区域，就会导致下面的 View 绘制到圆角区域
                aboveShowBottomRow = row - 1
                break
              }
            }
            if (startRow >= row && endRow <= aboveShowBottomRow) {
              // 排除掉会显示在外面的 View，不然会遮挡圆角
              isShow = true
              view = NetLayout(context).apply { setRowColumnCount(length, 1) }
              view.addView(
                createView(context, startNode, length),
                createNetLayoutParams(startRow, endRow)
              )
              return@forEachRow
            }
          }
        }
      }
    }
    if (isShow) {
      onAddIntoCourse()
    }
    return isShow
  }
  
  final override fun getAboveItem(row: Int): IOverlapItem? {
    return mAboveItemByRow.get(row)
  }
  
  final override fun getBelowItem(row: Int): IOverlapItem? {
    return mBelowItemByRow.get(row)
  }
}