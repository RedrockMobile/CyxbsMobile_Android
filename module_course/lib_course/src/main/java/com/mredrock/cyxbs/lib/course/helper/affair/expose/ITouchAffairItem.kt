package com.mredrock.cyxbs.lib.course.helper.affair.expose

import com.mredrock.cyxbs.lib.course.helper.affair.CreateAffairDispatcher
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
   * 是否正在显示
   */
  fun isInShow(): Boolean
  
  /**
   * 显示
   */
  fun show(
    topRow: Int,
    bottomRow: Int,
    initialColumn: Int
  )
  
  /**
   * 刷新
   */
  fun refresh(
    oldTopRow: Int,
    oldBottomRow: Int,
    topRow: Int,
    bottomRow: Int
  )
  
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