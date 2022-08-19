package com.mredrock.cyxbs.lib.course.internal.period

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 14:35
 */
enum class RowInclude {
  
  /**
   * 外离
   */
  OUTSIDE,
  
  /**
   * item 完全包含 period
   */
  CONTAIN_PERIOD,
  
  /**
   * period 完全包含 item
   */
  CONTAIN_ITEM,
  
  /**
   * 上边界相交
   */
  INTERSECT_TOP,
  
  /**
   * 下边界相交
   */
  INTERSECT_BOTTOM,
}

enum class ColumnInclude {
  
  /**
   * 外离
   */
  OUTSIDE,
  
  /**
   * item 完全包含 period
   */
  CONTAIN_PERIOD,
  
  /**
   * period 完全包含 item
   */
  CONTAIN_ITEM,
  
  /**
   * 左边界相交
   */
  INTERSECT_LEFT,
  
  /**
   * 右边界相交
   */
  INTERSECT_RIGHT,
}