package com.mredrock.cyxbs.lib.course.fragment.course.expose.entrance

import android.view.animation.LayoutAnimationController

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/13 0:27
 */
interface IEntranceAnim {
  
  /**
   * course 入场动画，会给每个 course 都设置
   *
   * 但因为存在数据延迟和不好判断页面的原因，需要你手动调用 [startEntranceAnim]
   */
  fun getEntranceAnim(): LayoutAnimationController?
  
  /**
   * 手动触发一次入场动画
   */
  fun startEntranceAnim()
}