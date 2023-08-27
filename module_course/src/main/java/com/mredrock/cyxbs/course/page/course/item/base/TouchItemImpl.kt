package com.mredrock.cyxbs.course.page.course.item.base

import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.TouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.helper.click.ClickItemHelper
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock

/**
 * 设置 Item 通用 Touch 事件的基类
 *
 * ## 注意：
 * - 已通过 [ClickItemHelper] 实现多指触摸下的 item 点击事件
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