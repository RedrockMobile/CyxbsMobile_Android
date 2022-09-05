package com.mredrock.cyxbs.lib.course.internal.lesson

import com.mredrock.cyxbs.lib.course.internal.view.course.lp.ItemLayoutParams

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 19:12
 */
abstract class BaseLessonLayoutParams(
  override val weekNum: Int,
  override val startNode: Int,
  override val length: Int
) : ItemLayoutParams(
  startNode, 
  startNode + length - 1,
  weekNum,
  weekNum
), ILessonData {
  
  constructor(data: ILessonData) : this(data.weekNum, data.startNode, data.length)
  
  @Deprecated("内部变量", level = DeprecationLevel.HIDDEN)
  final override var startRow: Int
    get() = super<ItemLayoutParams>.startRow
    set(value) { super<ItemLayoutParams>.startRow = value }
  
  @Deprecated("内部变量", level = DeprecationLevel.HIDDEN)
  final override var endRow: Int
    get() = super<ItemLayoutParams>.endRow
    set(value) { super<ItemLayoutParams>.endRow = value }
  
  @Deprecated("内部变量", level = DeprecationLevel.HIDDEN)
  final override var startColumn: Int
    get() = super<ItemLayoutParams>.startColumn
    set(value) { super<ItemLayoutParams>.startColumn = value }
  
  @Deprecated("内部变量", level = DeprecationLevel.HIDDEN)
  final override var endColumn: Int
    get() = super<ItemLayoutParams>.endColumn
    set(value) { super<ItemLayoutParams>.endColumn = value }
}