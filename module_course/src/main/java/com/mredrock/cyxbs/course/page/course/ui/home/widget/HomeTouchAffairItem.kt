package com.mredrock.cyxbs.course.page.course.ui.home.widget

import android.content.Context
import com.mredrock.cyxbs.course.page.course.ui.home.widget.helper.TouchAffairTouchHelper
import com.mredrock.cyxbs.lib.course.helper.affair.view.TouchAffairView
import com.mredrock.cyxbs.lib.course.item.helper.expand.ISingleSideExpandable
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.TouchItemHelper

/**
 * 实现 [ITouchItem] 的 [TouchAffairView]
 *
 * @author 985892345
 * 2023/2/19 21:55
 */
class HomeTouchAffairItem(context: Context) : TouchAffairView(context), ITouchItem {
  
  // 改为 public 暴露给外面使用
  public override val mSideExpandable: ISingleSideExpandable
    get() = super.mSideExpandable
  
  override val touchHelper: ITouchItemHelper = TouchItemHelper(
    TouchAffairTouchHelper()
  )
}