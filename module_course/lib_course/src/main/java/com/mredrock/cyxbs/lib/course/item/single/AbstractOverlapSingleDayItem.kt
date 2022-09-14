package com.mredrock.cyxbs.lib.course.item.single

import android.content.Context
import android.util.SparseIntArray
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.core.util.forEach
import androidx.core.util.isNotEmpty
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlap
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlapItem
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.OverlapHelper
import com.mredrock.cyxbs.lib.course.internal.item.forEachRow
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.ndhzs.netlayout.attrs.NetLayoutParams
import com.ndhzs.netlayout.view.NetLayout

/**
 * 用于实现单列的可重叠 item
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
   * - 此时并没有被添加进父布局，并且不能保证一定就能添加成功
   */
  protected open fun onAddIntoCourse() {}
  
  /**
   * 添加进父布局是否成功的回调
   */
  override fun onAddIntoParentResult(isSuccess: Boolean) {}
  
  /**
   * 刷新重叠区域时的回调
   *
   * ## 注意
   * - 只有添加进父布局的才会收到回调
   */
  protected open fun onRefreshOverlap() {}
  
  /**
   * 需要清除重叠区域时的回调
   */
  override fun onClearOverlap() {}
  
  /**
   * 得到使用 [createView] 生成的所有 view 集合的迭代器
   */
  fun getChildIterable(): Iterable<View> {
    return object : Iterable<View> {
      override fun iterator(): Iterator<View> {
        return object : Iterator<View> {
  
          val iterator1 = mChildInFree.iterator()
          val iterator2 = mChildInParent.iterator()
  
          override fun hasNext(): Boolean {
            return iterator1.hasNext() || iterator2.hasNext()
          }
  
          override fun next(): View {
            return if (iterator1.hasNext()) iterator1.next() else iterator2.next()
          }
        }
      }
    }
  }
  
  /**
   * 得到使用 [createView] 生成但目前不在 [mView] 中显示的所有 view 集合的迭代器
   */
  fun getChildInFree(): List<View> {
    return mChildInFree
  }
  
  /**
   * 得到使用 [createView] 生成并在 [mView] 中显示的所有 view 集合的迭代器
   */
  fun getChildInParent(): List<View> {
    return mChildInParent
  }
  
  /**
   * 遍历空闲的区域
   */
  fun forEachFreeAreaMap(block: (startRow: Int, endRow: Int) -> Unit) {
    mFreeAreaMap.forEach { startRow, endRow ->
      block.invoke(startRow, endRow)
    }
  }
  
  /**
   * 用于实现折叠的 View
   *
   * [createView] 中得到的 View 都会添加进这个 view 中
   */
  private lateinit var mView: NetLayout
  
  @Suppress("LeakingThis")
  final override val overlap: IOverlap = OverlapHelper(this)
  
  final override fun initializeView(context: Context): View {
    if (!this::mView.isInitialized) {
      mView = NetLayout(context).apply {
        setRowColumnCount(lp.rowCount, lp.columnCount)
      }
    } else {
      mView.setRowColumnCount(lp.rowCount, lp.columnCount)
    }
    return mView
  }
  
  final override fun isAddIntoParent(): Boolean {
    refreshFreeArea()
    if (mFreeAreaMap.isNotEmpty()) {
      onAddIntoCourse()
      return true
    }
    return false
  }
  
  private val mChildInFree = arrayListOf<View>()
  private val mChildInParent = arrayListOf<View>()
  
  
  final override fun refreshOverlap() {
    // 这里需要重新设置一遍总行数，因为外面可能重新设置了 lp，但 mView 的属性与 lp 并没有相互绑定
    mView.setRowColumnCount(lp.rowCount, lp.columnCount)
    refreshFreeArea()
    repeat(mChildInParent.size - mFreeAreaMap.size()) {
      val view = mChildInParent.removeLast()
      // 执行淡出动画
      val animation = AlphaAnimation(1F, 0F).apply { duration = 360 }
      mView.startAnimation(animation)
      mView.removeView(view)
      mChildInFree.add(view)
    }
    repeat(mFreeAreaMap.size() - mChildInParent.size) {
      val view = mChildInFree.removeLastOrNull() ?: createView(mView.context)
      val params = view.layoutParams as? NetLayoutParams ?: createNetLayoutParams()
      mView.addItem(view, params)
      mChildInParent.add(view)
    }
    repeat(mFreeAreaMap.size()) {
      val startRow = mFreeAreaMap.keyAt(it)
      val endRow = mFreeAreaMap.valueAt(it)
      val view = mChildInParent[it]
      val params = view.layoutParams as NetLayoutParams
      params.startColumn = 0
      params.endColumn = 0
      params.startRow = startRow - lp.startRow
      params.endRow = endRow - lp.startRow
      view.layoutParams = params
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
  
  private val mFreeAreaMap = SparseIntArray()
  
  /**
   * 刷新空闲区域
   */
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
          mFreeAreaMap.put(s, e)
        }
        s = row + 1
      }
    }
    // 判断 e 是不是最后一格，如果是的话，就要单独加上
    if (e == lp.endRow) {
      mFreeAreaMap.put(s, e)
    }
  }
}