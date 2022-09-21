package com.mredrock.cyxbs.lib.course.helper.affair

import com.mredrock.cyxbs.lib.course.helper.affair.view.TouchAffairView
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.item.single.ISingleDayItem
import com.mredrock.cyxbs.lib.course.item.single.SingleDayLayoutParams

/**
 * .
 *
 * @author 985892345
 * @date 2022/9/19 15:01
 */
interface ITouchAffair : ISingleDayItem {
  
  fun isInShow(): Boolean
  
  fun show(
    topRow: Int,
    bottomRow: Int,
    initialColumn: Int
  )
  
  fun refresh(
    oldTopRow: Int,
    oldBottomRow: Int,
    topRow: Int,
    bottomRow: Int
  )
  
  fun remove()
  
  fun cloneLp(): SingleDayLayoutParams
  
  companion object {
    
    /**
     * 工厂模式（虽然只有一个实现类）
     */
    fun getImpl(course: ICourseViewGroup): ITouchAffair {
      return TouchAffairView(course)
    }
  }
}