package com.mredrock.cyxbs.course.page.course.item

import android.content.Context
import android.view.View
import com.mredrock.cyxbs.course.page.course.data.ICourseItemData
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.course.page.course.item.view.IOverlapTag
import com.mredrock.cyxbs.course.page.course.ui.dialog.CourseBottomDialog
import com.mredrock.cyxbs.lib.base.utils.Umeng
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlapItem
import com.mredrock.cyxbs.lib.course.internal.item.forEachRow
import com.mredrock.cyxbs.lib.course.item.single.AbstractOverlapSingleDayItem
import com.mredrock.cyxbs.lib.course.item.single.ISingleDayData
import com.mredrock.cyxbs.lib.course.item.single.SingleDayLayoutParams
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.ndhzs.netlayout.view.NetLayout
import java.util.TreeSet

/**
 * 支持重叠的 item 的父类，只能用于单列显示的 item
 *
 * ## 注意
 * - item 的点击事件统一写在了这个父类里面
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:42
 */
abstract class BaseOverlapSingleDayItem<V, D> : AbstractOverlapSingleDayItem(),
  ISingleDayRank,
  ISingleDayData,
  IWeek // 用于整学期界面的周数排序
where V : View, V :IOverlapTag, D : ISingleDayData, D : IWeek // 用于提醒子类需要实现这些接口
{
  abstract val data: D
  
  abstract override val lp: SingleDayLayoutParams
  
  abstract override fun createView(context: Context): V
  
  abstract val isHomeCourseItem: Boolean // 是否是主页课表的 item
  
  override fun compareTo(other: IOverlapItem): Int {
    return if (other is ISingleDayRank) compareToInternal(other) else 1
  }
  
  override fun onRefreshOverlap() {
    super.onRefreshOverlap()
    var isNeedShowOverlapTag = false
    lp.forEachRow { row ->
      if (overlap.getBelowItem(row, lp.singleColumn) != null) {
        isNeedShowOverlapTag = true
        return@forEachRow
      }
    }
    getChildIterable().forEach {
      if (it is IOverlapTag) {
        it.setIsShowOverlapTag(isNeedShowOverlapTag)
      }
    }
  }
  
  override fun onClearOverlap() {
    super.onClearOverlap()
    getChildIterable().forEach {
      if (it is IOverlapTag) {
        it.setIsShowOverlapTag(false)
      }
    }
  }
  
  private var mLastShowDialog: CourseBottomDialog? = null
  
  override fun onInitializeView(view: NetLayout) {
    super.onInitializeView(view)
    // 设置点击事件，这里把 course 模块中所有的 item 都统一设置了
    val itemData = data
    if (itemData is ICourseItemData) {
      /*
      * item 的点击事件统一在这里设置
      * */
      view.setOnSingleClickListener {
        showCourseBottomDialog()
      }
    }
  }
  
  /**
   * 显示点击课表 item 后的 [CourseBottomDialog]
   */
  protected open fun showCourseBottomDialog() {
    val context = getView()?.context ?: return
    val treeSet = TreeSet<BaseOverlapSingleDayItem<*, *>> { o1, o2 ->
      o2.compareTo(o1) // 这里需要逆序
    }
    treeSet.add(this) // 别忘了添加自己
    lp.forEachRow { row ->
      // 寻找重叠在下面的所有 item
      var item = overlap.getBelowItem(row, lp.singleColumn)
      while (item != null) {
        if (item is BaseOverlapSingleDayItem<*, *>) {
          treeSet.add(item)
        }
        item = item.overlap.getBelowItem(row, lp.singleColumn)
      }
    }
    mLastShowDialog?.dismiss()
    CourseBottomDialog(
      context,
      treeSet.mapNotNull { item ->
        item.data as? ICourseItemData
      },
      isHomeCourseItem
    ).apply {
      mLastShowDialog = this
      show()
    }
  
    // Umeng 埋点统计
    Umeng.sendEvent(Umeng.Event.ClickCourseItem(false))
  }
  
  final override val weekNum: Int
    get() = data.weekNum
  final override val startNode: Int
    get() = data.startNode
  final override val length: Int
    get() = data.length
  final override val week: Int
    get() = data.week
}