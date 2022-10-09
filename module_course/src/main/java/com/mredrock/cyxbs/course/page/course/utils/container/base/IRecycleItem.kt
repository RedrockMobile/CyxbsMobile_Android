package com.mredrock.cyxbs.course.page.course.utils.container.base

import com.mredrock.cyxbs.lib.course.internal.item.IItem

/**
 * 支持回收的 item
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/9/29 16:36
 */
interface IRecycleItem : IItem {
  
  /**
   * 开始回收时调用
   *
   * 回收后会一直保存该对象，直到顶层父 View 被摧毁时才释放内存
   *
   * @return 能否回收，返回 true，则可以回收，返回 false，则会直接丢弃
   */
  fun onRecycle(): Boolean
  
  /**
   * 重新使用时回调
   * @return 返回能否重新使用，返回 true，则允许重新使用
   */
  fun onReuse(): Boolean
}