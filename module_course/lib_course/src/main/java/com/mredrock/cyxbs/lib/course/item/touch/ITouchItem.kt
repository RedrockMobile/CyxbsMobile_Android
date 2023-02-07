package com.mredrock.cyxbs.lib.course.item.touch

import com.mredrock.cyxbs.lib.course.fragment.page.base.CourseDefaultTouchImpl
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher

/**
 * 支持自定义事件处理的 item
 *
 * ## 注意
 * - 该类 item 接受到的是 course 的事件分发
 * - 为了不与 [IPointerDispatcher] 冲突，该事件分发的优先级最低，具体实现可查看 [CourseDefaultTouchImpl]
 *
 * @author 985892345
 * 2023/2/6 13:50
 */
interface ITouchItem : IItem {
  val touchHelper: ITouchItemHelper
}