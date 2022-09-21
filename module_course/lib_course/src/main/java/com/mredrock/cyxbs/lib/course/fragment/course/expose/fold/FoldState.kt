package com.mredrock.cyxbs.lib.course.fragment.course.expose.fold

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 0:47
 */
enum class FoldState {
  FOLD, // 完全折叠
  UNFOLD, // 完全展开
  FOLD_ANIM, // 处于折叠动画中
  UNFOLD_ANIM, // 处于展开动画中
  UNKNOWN // 未知状态
}