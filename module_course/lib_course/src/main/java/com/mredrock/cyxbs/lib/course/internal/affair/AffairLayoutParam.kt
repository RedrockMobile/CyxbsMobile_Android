package com.mredrock.cyxbs.lib.course.internal.affair

import com.mredrock.cyxbs.lib.course.internal.view.course.lp.CourseLayoutParam

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/19 14:05
 */
open class AffairLayoutParam(
  override val weekNum: Int,
  override val startNode: Int,
  override val length: Int
) : CourseLayoutParam(
  startNode,
  startNode + length - 1,
  weekNum,
  weekNum
), IAffairData {
  
  @Deprecated("内部变量", level = DeprecationLevel.HIDDEN)
  final override var startRow: Int
    get() = super<CourseLayoutParam>.startRow
    set(value) { super<CourseLayoutParam>.startRow = value }
  
  @Deprecated("内部变量", level = DeprecationLevel.HIDDEN)
  final override var endRow: Int
    get() = super<CourseLayoutParam>.endRow
    set(value) { super<CourseLayoutParam>.endRow = value }
  
  @Deprecated("内部变量", level = DeprecationLevel.HIDDEN)
  final override var startColumn: Int
    get() = super<CourseLayoutParam>.startColumn
    set(value) { super<CourseLayoutParam>.startColumn = value }
  
  @Deprecated("内部变量", level = DeprecationLevel.HIDDEN)
  final override var endColumn: Int
    get() = super<CourseLayoutParam>.endColumn
    set(value) { super<CourseLayoutParam>.endColumn = value }
}