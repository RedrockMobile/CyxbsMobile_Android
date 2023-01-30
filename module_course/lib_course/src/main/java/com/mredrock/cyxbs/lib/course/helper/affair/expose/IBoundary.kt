package com.mredrock.cyxbs.lib.course.helper.affair.expose

import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup

/**
 * 计算能够滑动的上下边界区域
 *
 * @author 985892345
 * 2023/1/30 16:00
 */
interface IBoundary {
  
  /**
   * 得到上边界
   * @param initialRow 手指刚触摸时的行数
   * @param nowRow 当前手指触摸的行数
   * @param initialColumn 手指刚触摸时的列数
   */
  fun getUpperRow(course: ICourseViewGroup, initialRow: Int, nowRow: Int, initialColumn: Int): Int
  
  /**
   * 得到下边界
   * @param initialRow 手指刚触摸时的行数
   * @param nowRow 当前手指触摸的行数
   * @param initialColumn 手指刚触摸时的列数
   */
  fun getLowerRow(course: ICourseViewGroup, initialRow: Int, nowRow: Int, initialColumn: Int): Int
}