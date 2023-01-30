package com.mredrock.cyxbs.lib.course.helper.affair.expose

import androidx.core.view.isGone
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.helper.affair.CreateAffairDispatcher
import com.mredrock.cyxbs.lib.course.helper.affair.view.TouchAffairView
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.internal.view.course.lp.ItemLayoutParams
import kotlin.math.max
import kotlin.math.min

/**
 * [CreateAffairDispatcher] 与外界进行交互的接口
 *
 * ## 该类作用
 * 深度定制 [CreateAffairDispatcher] 的一些行为，减少前者的代码耦合度
 *
 * @author 985892345
 * 2023/1/30 16:40
 */
interface ICreateAffair : IBoundary {
  
  /**
   * 当前 Down 的触摸点是否合法，合法才会拦截触摸事件并生成 [ITouchAffairItem]
   */
  fun isValidDown(page: ICoursePage, x: Int, y: Int): Boolean
  
  /**
   * 创建 [ITouchAffairItem]，可以继承于 [TouchAffairView]
   *
   * 如果返回 null 的话将不会生成任何 item
   */
  fun createTouchAffairItem(course: ICourseViewGroup): ITouchAffairItem?
  
  
  companion object Default : ICreateAffair {
    
    private var _upperRow = Int.MIN_VALUE // 临时变量
    private var _lowerRow = Int.MAX_VALUE // 临时变量
    
    override fun isValidDown(page: ICoursePage, x: Int, y: Int): Boolean {
      if (x > page.getTimelineEndWidth()) {
        // 触摸位置大于左边时间轴的宽度时
        if (page.course.findPairUnderByXY(x, y) == null) {
          // 当前触摸的是空白位置时才准备拦截
          return true
        }
      }
      return false
    }
  
    override fun createTouchAffairItem(course: ICourseViewGroup): ITouchAffairItem {
      return TouchAffairView(course)
    }
  
    override fun getUpperRow(
      course: ICourseViewGroup,
      initialRow: Int,
      nowRow: Int,
      initialColumn: Int,
    ): Int {
      calculateUpperLowerRow(course, initialRow, nowRow, initialColumn)
      return _upperRow
    }
  
    override fun getLowerRow(
      course: ICourseViewGroup,
      initialRow: Int,
      nowRow: Int,
      initialColumn: Int,
    ): Int {
      calculateUpperLowerRow(course, initialRow, nowRow, initialColumn)
      return _lowerRow
    }
  
    /**
     * 计算 [_upperRow] 和 [_lowerRow]
     */
    private fun calculateUpperLowerRow(
      course: ICourseViewGroup,
      initialRow: Int,
      nowRow: Int,
      initialColumn: Int,
    ) {
      _upperRow = 0
      _lowerRow = course.rowCount - 1
      for ((child, _) in course.getItemByViewMap()) {
        if (child.isGone) continue
        val lp = child.layoutParams as ItemLayoutParams
        if (initialColumn in lp.startColumn..lp.endColumn) {
          when {
            initialRow > lp.endRow -> _upperRow = max(_upperRow, lp.endRow + 1)
            initialRow < lp.startRow -> _lowerRow = min(_lowerRow, lp.startRow - 1)
            else -> continue // 此时触摸点在一个 item 里面，这应该由你自己的逻辑单独处理
          }
        }
      }
    }
  }
}