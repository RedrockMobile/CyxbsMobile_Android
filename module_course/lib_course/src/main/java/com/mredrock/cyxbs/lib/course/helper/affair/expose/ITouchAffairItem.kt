package com.mredrock.cyxbs.lib.course.helper.affair.expose

import com.mredrock.cyxbs.lib.course.helper.affair.CreateAffairDispatcher
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.item.single.ISingleDayItem
import com.mredrock.cyxbs.lib.course.item.single.SingleDayLayoutParams

/**
 * 在长按空白区域时可以生成一个 item
 *
 * @author 985892345
 * @date 2022/9/19 15:01
 */
interface ITouchAffairItem : ISingleDayItem {
  
  /**
   * 移动准备开始时的回调
   * @param row 开始时触摸的行数
   * @param column 开始时触摸的列数
   */
  fun onMoveStart(course: ICourseViewGroup, row: Int, column: Int)
  
  /**
   * 移动中
   * @param row 固定的那一边的行数
   * @param y 另一边的 y 值，相对于 course
   */
  fun onSingleMove(course: ICourseViewGroup, row: Int, y: Int)
  
  /**
   * 移动结束
   */
  fun onMoveEnd(course: ICourseViewGroup)
  
  /**
   * 是否正在显示
   */
  fun isInShow(): Boolean
  
  /**
   * 取消显示
   */
  fun cancelShow()
  
  /**
   * 克隆 [lp]
   */
  fun cloneLp(): SingleDayLayoutParams
  
  /**
   * 注意：这个监听在 [CreateAffairDispatcher] 中统一设置，不建议私自调用
   */
  fun setOnClickListener(l: () -> Unit)
}