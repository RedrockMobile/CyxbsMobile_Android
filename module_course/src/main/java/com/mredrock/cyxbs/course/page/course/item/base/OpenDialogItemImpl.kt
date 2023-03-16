package com.mredrock.cyxbs.course.page.course.item.base

import com.mredrock.cyxbs.course.page.course.data.ICourseItemData
import com.mredrock.cyxbs.course.page.course.ui.dialog.CourseBottomDialog
import com.mredrock.cyxbs.lib.base.utils.Umeng
import com.mredrock.cyxbs.lib.course.internal.item.forEachRow
import com.mredrock.cyxbs.lib.course.item.single.AbstractOverlapSingleDayItem
import java.util.*

/**
 * .
 *
 * @author 985892345
 * 2023/2/22 12:43
 */
abstract class OpenDialogItemImpl : AbstractOverlapSingleDayItem() {
  
  abstract val iCourseItemData: ICourseItemData
  
  abstract val isHomeCourseItem: Boolean // 是否是主页课表的 item
  
  private var mLastShowDialog: CourseBottomDialog? = null
  
  /**
   * 显示点击课表 item 后的 [CourseBottomDialog]
   */
  protected open fun showCourseBottomDialog() {
    val context = getView()?.context ?: return
    val treeSet = TreeSet<OpenDialogItemImpl> { o1, o2 ->
      o2.compareTo(o1) // 这里需要逆序
    }
    treeSet.add(this) // 别忘了添加自己
    lp.forEachRow { row ->
      // 寻找重叠在下面的所有 item
      var item = overlap.getBelowItem(row, lp.singleColumn)
      while (item != null) {
        if (item is OpenDialogItemImpl) {
          treeSet.add(item)
        }
        item = item.overlap.getBelowItem(row, lp.singleColumn)
      }
    }
    mLastShowDialog?.dismiss()
    mLastShowDialog = CourseBottomDialog(
      context,
      treeSet.map { item ->
        item.iCourseItemData
      },
      isHomeCourseItem // 主页课表才需要显示关联人
    ).apply {
      show()
    }
  
    // Umeng 埋点统计
    Umeng.sendEvent(Umeng.Event.CourseDetail(false))
  }
}