package com.mredrock.cyxbs.lib.course.internal.item

/**
 * 只有一列的 Item
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 16:02
 */
interface ISingleColumnItemData : IItemData {
  
  val singleColumn: Int
  
  @Deprecated("不建议调用", level = DeprecationLevel.HIDDEN)
  override val startColumn: Int
    get() = singleColumn
  
  @Deprecated("不建议调用", level = DeprecationLevel.HIDDEN)
  override val endColumn: Int
    get() = singleColumn
}