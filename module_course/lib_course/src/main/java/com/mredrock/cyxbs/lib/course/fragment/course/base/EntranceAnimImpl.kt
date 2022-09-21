package com.mredrock.cyxbs.lib.course.fragment.course.base

import android.os.Bundle
import android.view.View
import android.view.animation.LayoutAnimationController
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.course.fragment.course.expose.entrance.IEntranceAnim

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/13 0:27
 */
abstract class EntranceAnimImpl : ContainerImpl(), IEntranceAnim {
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    course.setLayoutAnimation(getEntranceAnim())
  }
  
  override fun getEntranceAnim(): LayoutAnimationController? {
//    return LayoutAnimationController(
//      AnimationUtils.loadAnimation(
//        context,
//        R.anim.course_anim_entrance
//      ), 0.2F
//    )
    return null
  }
  
  override fun startEntranceAnim() {
    course.startLayoutAnimation()
  }
}