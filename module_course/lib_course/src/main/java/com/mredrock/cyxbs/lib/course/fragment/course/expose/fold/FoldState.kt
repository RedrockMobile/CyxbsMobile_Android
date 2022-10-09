package com.mredrock.cyxbs.lib.course.fragment.course.expose.fold

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 0:47
 */
enum class FoldState {
  /**
   * 完全折叠
   */
  FOLD,
  
  /**
   * 完全展开
   */
  UNFOLD,
  
  /**
   * 处于折叠动画中
   */
  FOLD_ANIM,
  
  /**
   * 处于展开动画中
   */
  UNFOLD_ANIM,
  
  /**
   * 未知状态
   */
  UNKNOWN
}