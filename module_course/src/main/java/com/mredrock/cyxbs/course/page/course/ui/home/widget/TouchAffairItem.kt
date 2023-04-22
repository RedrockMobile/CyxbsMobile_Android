package com.mredrock.cyxbs.course.page.course.ui.home.widget

import android.annotation.SuppressLint
import com.mredrock.cyxbs.lib.course.helper.affair.view.TouchAffairView
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.TouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.MovableItemHelper

/**
 * .
 *
 * @author 985892345
 * 2023/2/19 21:55
 */
@SuppressLint("ViewConstructor")
class TouchAffairItem(course: ICourseViewGroup) : TouchAffairView(course), ITouchItem {
  override val touchHelper: ITouchItemHelper = TouchItemHelper(
    MovableItemHelper()
  )
}