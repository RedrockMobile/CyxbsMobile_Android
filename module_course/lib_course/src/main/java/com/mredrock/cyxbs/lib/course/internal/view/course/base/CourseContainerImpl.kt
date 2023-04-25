package com.mredrock.cyxbs.lib.course.internal.view.course.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer
import com.mredrock.cyxbs.lib.course.utils.forEachReversed
import com.ndhzs.netlayout.attrs.NetLayoutParams
import com.ndhzs.netlayout.child.OnChildExistListener
import java.util.Collections
import kotlin.collections.ArrayList

/**
 * [IItemContainer] 的实现类
 *
 * 功能如下：
 * - 实现 [IItem] 的添加
 * - 关联 View 的移除回调，在 View 被移除时同时移除 Item
 * - 提供监听 Item 添加、移除的方法
 * - 提供查找 Item 的方法
 *
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
  IItemContainer {
  
  private val mOnItemExistListeners = ArrayList<IItemContainer.OnItemExistListener>(2)
  private val mItemInterceptors = ArrayList<IItemContainer.IItemInterceptor>(2)
  private val mItemByView = hashMapOf<View, IItem>()
  private val mViewByItem = hashMapOf<IItem, View>()
  private val mInterceptorByItem = hashMapOf<IItem, IItemContainer.IItemInterceptor>()
  
  init {
    // 使用其他方式删除子 View 时也需要同步删除 item
    addChildExistListener(
      object : OnChildExistListener {
        override fun onChildViewRemoved(parent: ViewGroup, child: View) {
          val item = mItemByView[child] ?: return
          removeItem(item)
        }
      }
    )
  }
  
  final override fun addItemExistListener(l: IItemContainer.OnItemExistListener) {
    mOnItemExistListeners.add(l)
  }
  
  final override fun removeItemExistListener(l: IItemContainer.OnItemExistListener) {
    mOnItemExistListeners.remove(l)
  }
  
  final override fun addItemInterceptor(interceptor: IItemContainer.IItemInterceptor) {
    mItemInterceptors.add(interceptor)
  }
  
  final override fun addItem(item: IItem) {
    check(!mViewByItem.contains(item)) { "该 View 已经被添加了" }
    mInterceptorByItem.remove(item) // 需要先删除关联的拦截器，因为可能是第二次添加
    mOnItemExistListeners.forEachReversed { it.onItemAddedBefore(item) }
    var isIntercept = false
    for (it in mItemInterceptors) {
      if (it.addItem(item)) {
        isIntercept = true
        mInterceptorByItem[item] = it
        break
      }
    }
    var view: View? = null
    if (!isIntercept) {
      view = item.initializeView(context)
      mItemByView[view] = item
      mViewByItem[item] = view
      // 此时才能添加进课表
      super.addNetChild(view, item.lp)
    }
    mOnItemExistListeners.forEachReversed { it.onItemAddedAfter(item, view) }
  }
  
  final override fun removeItem(item: IItem) {
    val interceptor = mInterceptorByItem.remove(item)
    if (interceptor != null) {
      mOnItemExistListeners.forEachReversed { it.onItemRemovedBefore(item, null) }
      interceptor.removeItem(item)
      mOnItemExistListeners.forEachReversed { it.onItemRemovedAfter(item, null) }
    } else {
      val view = mViewByItem[item]
      if (view != null) {
        mOnItemExistListeners.forEachReversed { it.onItemRemovedBefore(item, view) }
        mItemByView.remove(view)
        mViewByItem.remove(item)
        super.removeView(view) // 这一步需要在最后
        mOnItemExistListeners.forEachReversed { it.onItemRemovedAfter(item, view) }
      }
    }
  }
  
  final override fun getItemByView(view: View?): IItem? {
    return mItemByView[view]
  }
  
  final override fun getViewByItem(item: IItem?): View? {
    return mViewByItem[item]
  }
  
  override fun getItemByViewMap(): Map<View, IItem> {
    return Collections.unmodifiableMap(mItemByView)
  }
  
  override fun getViewByItemMap(): Map<IItem, View> {
    return Collections.unmodifiableMap(mViewByItem)
  }
  
  override fun findItemUnderByXY(x: Int, y: Int): IItem? {
    val view = findViewUnderByXY(x, y)
    return getItemByView(view)
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