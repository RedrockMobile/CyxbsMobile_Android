package com.mredrock.cyxbs.course.page.course.item.base

import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.TouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.helper.click.ClickItemHelper
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock

/**
 * .
 *
 * @author 985892345
 * 2023/2/22 12:18
 */
abstract class TouchItemImpl : SingleDayItemImpl(), ITouchItem {
  
  final override val touchHelper: ITouchItemHelper by lazyUnlock {
    TouchItemHelper(*initializeTouchItemHelper().toTypedArray())
  }
  
  open fun initializeTouchItemHelper(): List<ITouchItemHelper> {
    return listOf(
      // 默认添加了点击 item 的监听
      ClickItemHelper {
        showCourseBottomDialog()
      }
    )
  }
}