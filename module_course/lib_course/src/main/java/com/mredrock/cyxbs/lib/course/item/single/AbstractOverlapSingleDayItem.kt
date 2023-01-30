package com.mredrock.cyxbs.lib.course.item.single

import android.content.Context
import android.util.SparseIntArray
import android.view.View
import androidx.core.util.forEach
import androidx.core.util.isNotEmpty
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.fragment.course.base.OverlapImpl
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlap
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlapItem
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.OverlapHelper
import com.mredrock.cyxbs.lib.course.internal.item.forEachRow
import com.mredrock.cyxbs.lib.utils.extensions.dimen
import com.ndhzs.netlayout.attrs.NetLayoutParams
import com.ndhzs.netlayout.view.NetLayout
import java.util.Collections

/**
 * 用于实现单列的可重叠 item
 *
 * ## 重叠是什么？
 * 比如我有一个 1、2、3、4 节的课，但是另一节课是在 2、3 节，此时就会导致一节课被分开展示
 *
 * ## 怎么解决被分开展示的问题？
 * 按照产品给的需求：这分开的区域每个都要单独显示，且显示的形式与 1、2、3、4 节相同，只不过大小不同。
 *
 * 根据上述需求，一个 item 可以由多行组成（当前未考虑多列），我将 item 对应的 view 设为了 NetLayout，
 * 在 NetLayout 里面填充多个相同的子 View 来实现分开展示
 *
 * ## 怎么计算重叠的？
 * 重叠的计算具体逻辑请看 [OverlapImpl]
 *
 * 这里讲一下大致思路：课表是网格布局，每个格子都有一条引用链，串联起了这个格子上的所有 item。
 * 正常情况下只会显示最顶上的 item，如果你将顶上的 item removed 或者 gone，引用链会重新计算并刷新显示
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/26 17:00
 */
abstract class AbstractOverlapSingleDayItem : IOverlapItem, OverlapHelper.IOverlapLogic, ISingleDayItem {
  
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
    return Collections.unmodifiableList(mChildInFree)
  }
  
  /**
   * 得到使用 [createView] 生成并在 [mView] 中显示的所有 view 集合的迭代器
   */
  fun getChildInParent(): List<View> {
    return Collections.unmodifiableList(mChildInParent)
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
   * 初始化 View 的回调
   */
  protected open fun onInitializeView(view: NetLayout) {}
  
  /**
   * 得到当前 item 的 View
   */
  fun getView(): NetLayout? {
    return if (this::mView.isInitialized) mView else null
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
    onInitializeView(mView)
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
    // 刷新空闲区域
    refreshFreeArea()
    // 移除掉多的子 View
    repeat(mChildInParent.size - mFreeAreaMap.size()) {
      val view = mChildInParent.removeLast()
      mView.removeView(view)
      mChildInFree.add(view)
    }
    // 添加新的子 View
    repeat(mFreeAreaMap.size() - mChildInParent.size) {
      val view = mChildInFree.removeLastOrNull() ?: createView(mView.context)
      val params = view.layoutParams as? NetLayoutParams ?: createNetLayoutParams()
      mView.addNetChild(view, params)
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
      leftMargin = R.dimen.course_item_margin.dimen.toInt()
      rightMargin = leftMargin
      topMargin = leftMargin
      bottomMargin = leftMargin
    }
  }
  
  private val mFreeAreaMap = SparseIntArray()
  
  /**
   * 刷新空闲区域，重新计算自己可以显示的节点
   */
  private fun refreshFreeArea() {
    mFreeAreaMap.clear()
    val column = lp.weekNum
    var s = lp.startRow
    var e = s - 1
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