package com.mredrock.cyxbs.lib.course.item.single

import android.content.Context
import android.view.View
import androidx.collection.ArrayMap
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlap
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlapItem
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.OverlapHelper
import com.mredrock.cyxbs.lib.course.internal.item.forEachRow
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.ndhzs.netlayout.attrs.NetLayoutParams
import com.ndhzs.netlayout.view.NetLayout2

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/26 17:00
 */
abstract class AbstractOverlapSingleDayItem : IOverlapItem, OverlapHelper.ILogic, ISingleDayItem {
  
  /**
   * 创建子 View，这个 View 才是真正用于显示的
   *
   * 因为存在重叠分开显示的情况，所以需要单独用子 View 来实现（比如：1 2 3 4 节课，中间的 2 3 节课被遮挡了）
   */
  protected abstract fun createView(context: Context): View
  
  /**
   * 即将被添加进父布局时的回调
   *
   * ## 注意
   * 此时并没有被添加进父布局
   */
  protected open fun onAddIntoCourse() {}
  
  /**
   * 刷新重叠区域时的回调
   */
  protected open fun onRefreshOverlap() {}
  
  /**
   * 需要清除重叠区域时的回调
   */
  override fun onClearOverlap() {}
  
  /**
   * 得到 [createView] 创造的所有 view
   */
  fun getChildren(): List<View> {
    return mChildren
  }
  
  /**
   * 得到空闲的区域，第一个 Int 为 startRow，第二个为 endRow
   */
  fun getFreeAreaMap(): Map<Int, Int> {
    return mFreeAreaMap
  }
  
  /**
   * 用于实现折叠的 View
   *
   * [createView] 中得到的 View 都会添加进这个 view 中
   */
  private lateinit var mView: NetLayout2
  
  @Suppress("LeakingThis")
  final override val overlap: IOverlap = OverlapHelper(this)
  
  private val mChildren = arrayListOf<View>()
  
  final override fun initializeView(context: Context): View {
    if (this::mView.isInitialized) {
      mView.removeAllViews()
    } else {
      mView = NetLayout2(context).apply {
        setRowColumnCount(lp.rowCount, lp.columnCount)
      }
    }
    return mView
  }
  
  final override fun isAddIntoParent(): Boolean {
    refreshFreeArea()
    if (mFreeAreaMap.isNotEmpty()) {
      return true
    }
    return false
  }
  
  final override fun refreshOverlap() {
    mFreeAreaMap.forEach { (row, column) ->
      var view = mChildren.removeLastOrNull()
      if (view != null && view.parent === mView) {
        val params = view.layoutParams as NetLayoutParams
        params.startRow = row - lp.startRow
        params.endRow = column - lp.startRow
        view.layoutParams = params
      } else {
        view = createView(mView.context)
        val params = createNetLayoutParams()
        params.startRow = row - lp.startRow
        params.endRow = column - lp.startRow
        mView.addItem(view, params)
      }
      mChildren.add(view)
    }
    onRefreshOverlap()
  }
  
  /**
   * 创建子 View 的 [NetLayoutParams]
   */
  private fun createNetLayoutParams(): NetLayoutParams {
    return NetLayoutParams(0, 0,0, 0).apply {
      leftMargin = 1.2F.dp2px
      rightMargin = leftMargin
      topMargin = leftMargin
      bottomMargin = leftMargin
    }
  }
  
  private val mFreeAreaMap = ArrayMap<Int, Int>()
  
  private fun refreshFreeArea() {
    mFreeAreaMap.clear()
    val column = lp.weekNum
    var s = lp.startRow
    var e = s - 1
    // 实现重叠后分开显示的逻辑
    lp.forEachRow { row ->
      if (overlap.isDisplayable(row, column)) {
        e = row
      } else {
        if (s <= e) {
          mFreeAreaMap[s] = e
        }
        s = row + 1
      }
    }
    // 判断 e 是不是最后一格，如果是的话，就要单独加上
    if (e == lp.endRow) {
      mFreeAreaMap[s] = e
    }
    if (mFreeAreaMap.isEmpty) {
      lp.forEachRow { row ->
        val above = overlap.getAboveItem(row, column)
        if (above != null) {
          if (above.overlap.getAboveItem(row, column) == null) {
            // 如果存在某一个位置处于第二层，则允许显示
            // 允许显示的原因是：点击顶上的 View 有一个 Q 弹动画，可以显示下面的 View
            var aboveShowBottomRow = above.lp.endRow
            for (r in (row + 1) .. above.lp.endRow) {
              if (above.overlap.getAboveItem(r, column) != null) {
                // 寻找到 above 从 row 开始往后的第一个有重叠的位置，这个位置到 row 的区域才可以用来显示下面的 View
                // 如果超过这个区域，就会导致下面的 View 绘制到圆角区域
                aboveShowBottomRow = row - 1
                break
              }
            }
            if (lp.startRow >= row && lp.endRow <= aboveShowBottomRow) {
              // 排除掉会显示在外面的 View，不然会遮挡圆角
              mFreeAreaMap[lp.startRow] = lp.endRow
              return@forEachRow
            }
//            mFreeAreaMap.put(lp.startRow, lp.endRow)
//            return@forEachRow
          }
        }
      }
    }
  }
}