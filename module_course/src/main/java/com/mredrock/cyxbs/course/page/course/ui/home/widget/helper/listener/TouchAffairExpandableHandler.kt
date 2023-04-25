package com.mredrock.cyxbs.course.page.course.ui.home.widget.helper.listener

import android.view.View
import com.mredrock.cyxbs.course.page.course.ui.home.widget.HomeTouchAffairItem
import com.mredrock.cyxbs.course.page.course.ui.home.widget.helper.TouchAffairTouchHelper
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.helper.expand.ISingleSideExpandable
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.helper.expand.utils.DefaultExpandableHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * [TouchAffairTouchHelper] 中 Expandable 的自定义 Handler
 *
 * @author 985892345
 * 2023/4/23 18:20
 */
class TouchAffairExpandableHandler : DefaultExpandableHandler() {
  override fun newSingleSIdeExpandable(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    event: IPointerEvent
  ): ISingleSideExpandable {
    return (item as HomeTouchAffairItem).mSideExpandable
  }
}