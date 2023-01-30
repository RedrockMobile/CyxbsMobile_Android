package com.mredrock.cyxbs.lib.course.internal.view.course.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer
import com.mredrock.cyxbs.lib.course.utils.forEachInline
import com.ndhzs.netlayout.attrs.NetLayoutParams

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 0:23
 */
@Suppress("DeprecatedCallableAddReplaceWith")
abstract class CourseContainerImpl @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = R.attr.courseLayoutStyle,
  defStyleRes: Int = 0
) : AbstractCourseViewGroup(context, attrs, defStyleAttr, defStyleRes),
  IItemContainer
{
  
  private val mOnItemExistListeners = ArrayList<IItemContainer.OnItemExistListener>(2)
  private val mItemByView = hashMapOf<View, IItem>()
  private val mViewByItem = hashMapOf<IItem, View>()
  
  final override fun addItemExistListener(l: IItemContainer.OnItemExistListener) {
    mOnItemExistListeners.add(l)
  }
  
  override fun postRemoveItemExistListener(l: IItemContainer.OnItemExistListener) {
    post {
      // 由于遍历中不能直接删除，所以使用 post 来延迟删除
      mOnItemExistListeners.remove(l)
    }
  }
  
  final override fun addItem(item: IItem): Boolean {
    if (mViewByItem.contains(item)) {
      error("该 View 已经被添加了，请检查所有调用该方法的地方！")
    }
    if (mOnItemExistListeners.all { it.isAllowToAddItem(item) }) {
      val view = item.initializeView(context)
      mItemByView[view] = item
      mViewByItem[item] = view
      mOnItemExistListeners.forEachInline { it.onItemAddedBefore(item, view) }
      super.addNetChild(view, item.lp)
      mOnItemExistListeners.forEachInline { it.onItemAddedAfter(item, view) }
      return true
    }
    mOnItemExistListeners.forEachInline { it.onItemAddedFail(item) }
    return false
  }
  
  final override fun removeItem(item: IItem): Boolean {
    val view = mViewByItem[item]
    if (view != null) {
      mOnItemExistListeners.forEachInline { it.onItemRemovedBefore(item, view) }
      super.removeView(view)
      mOnItemExistListeners.forEachInline { it.onItemRemovedAfter(item, view) }
      mItemByView.remove(view)
      mViewByItem.remove(item)
      return true
    }
    // 此时说明没有添加过该 item，但可能是因为之前的添加中有 listener 把它拦截了
    mOnItemExistListeners.forEachInline { it.onItemRemovedFail(item) }
    return false
  }
  
  final override fun getItemByView(view: View?): IItem? {
    return mItemByView[view]
  }
  
  final override fun getViewByItem(item: IItem?): View? {
    return mViewByItem[item]
  }
  
  override fun getItemByViewMap(): Map<View, IItem> {
    return mItemByView
  }
  
  override fun getViewByItemMap(): Map<IItem, View> {
    return mViewByItem
  }
  
  final override fun findPairUnderByXY(x: Int, y: Int): Pair<IItem, View>? {
    val view = findViewUnderByXY(x, y)
    val item = getItemByView(view)
    if (item != null && view != null) {
      return Pair(item, view)
    }
    return null
  }
  
  final override fun findPairUnderByFilter(filter: IItem.(View) -> Boolean): Pair<IItem, View>? {
    for (i in childCount - 1 downTo 0) {
      val child = getChildAt(i)
      val item = mItemByView[child]
      if (item != null) {
        if (filter.invoke(item, child)) {
          return Pair(item, child)
        }
      }
    }
    return null
  }
  
  
  @Deprecated("请使用 addItem()", level = DeprecationLevel.ERROR)
  final override fun addNetChild(child: View, lp: NetLayoutParams) {
    super.addNetChild(child, lp)
  }
  
  @Deprecated("请使用 addItem()", level = DeprecationLevel.ERROR)
  final override fun addView(child: View?) {
    super.addView(child)
  }
  
  @Deprecated("请使用 addItem()", level = DeprecationLevel.ERROR)
  final override fun addView(child: View?, index: Int) {
    super.addView(child, index)
  }
  
  @Deprecated("请使用 addItem()", level = DeprecationLevel.ERROR)
  final override fun addView(child: View?, width: Int, height: Int) {
    super.addView(child, width, height)
  }
  
  @Deprecated("请使用 addItem()", level = DeprecationLevel.ERROR)
  final override fun addView(child: View?, params: LayoutParams?) {
    super.addView(child, params)
  }
  
  @Deprecated("请使用 removeItem()", level = DeprecationLevel.ERROR)
  final override fun removeView(view: View?) {
    super.removeView(view)
  }
  
  @Deprecated("请使用 removeItem()", level = DeprecationLevel.ERROR)
  final override fun removeViewAt(index: Int) {
    super.removeViewAt(index)
  }
  
  @Deprecated("请使用 removeItem()", level = DeprecationLevel.ERROR)
  final override fun removeViews(start: Int, count: Int) {
    super.removeViews(start, count)
  }
}