package com.mredrock.cyxbs.lib.course.internal.view.course.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.collection.arrayMapOf
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer
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
  private val mItemByView = arrayMapOf<View, IItem>()
  private val mViewByItem = arrayMapOf<IItem, View>()
  
  final override fun addItemExistListener(l: IItemContainer.OnItemExistListener) {
    mOnItemExistListeners.add(l)
  }
  
  final override fun addItem(item: IItem): Boolean {
    if (mViewByItem.contains(item)) {
      // 包含的话说明已经被添加了
      return false
    }
    if (mOnItemExistListeners.all { it.isAllowToAddItem(item) }) {
      val view = item.initializeView(context)
      mItemByView[view] = item
      mViewByItem[item] = view
      mOnItemExistListeners.forEach { it.onItemAddedBefore(item) }
      super.addItem(view, item.lp)
      mOnItemExistListeners.forEach { it.onItemAddedAfter(item) }
      return true
    }
    return false
  }
  
  final override fun removeItem(item: IItem) {
    val view = mViewByItem[item]
    if (view != null) {
      mOnItemExistListeners.forEach { it.onItemRemovedBefore(item) }
      super.removeView(view)
      mOnItemExistListeners.forEach { it.onItemRemovedAfter(item) }
      mItemByView.remove(view)
      mViewByItem.remove(item)
    }
  }
  
  final override fun getItemByView(view: View?): IItem? {
    return mItemByView[view]
  }
  
  final override fun getViewByItem(item: IItem?): View? {
    return mViewByItem[item]
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
  
  
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.HIDDEN)
  final override fun addItem(item: View, lp: NetLayoutParams) {
    super.addItem(item, lp)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.HIDDEN)
  final override fun addView(child: View?) {
    super.addView(child)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.HIDDEN)
  final override fun addView(child: View?, index: Int) {
    super.addView(child, index)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.HIDDEN)
  final override fun addView(child: View?, width: Int, height: Int) {
    super.addView(child, width, height)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.HIDDEN)
  final override fun addView(child: View?, params: LayoutParams?) {
    super.addView(child, params)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.HIDDEN)
  final override fun removeView(view: View?) {
    super.removeView(view)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.HIDDEN)
  final override fun removeViewAt(index: Int) {
    super.removeViewAt(index)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.HIDDEN)
  final override fun removeViews(start: Int, count: Int) {
    super.removeViews(start, count)
  }
}