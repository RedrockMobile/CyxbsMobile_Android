//package com.mredrock.cyxbs.course2.page.course.helper.multitouch.entitymove
//
//import android.view.View
//
///**
// * ...
// * @author 985892345 (Guo Xiangrui)
// * @email 2767465918@qq.com
// * @date 2022/5/11 21:06
// */
//interface OnEntityMoveListener {
//
//  /**
//   * 长按开始时，此时长按已经触发，且 View 还没有添加进 Overlay 前回调
//   */
//  fun onMoveStart(pointerId: Int, view: View) {}
//
//  /**
//   * 长按触摸结束时，包括手指抬起和被前一个布局拦截的事件，他们都被认为是触摸结束的事件
//   *
//   * **注意：** 这里得到的 View 修改绘图这些是失效的，具体原因在于我在长按移动中将 View 添加进了 overlay，
//   * 可以去看看 [AffairMoveTouchHandler.putViewIntoOverlayIfCan] 方法
//   */
//  fun onMoveTouchEnd(pointerId: Int, view: View) {}
//
//  /**
//   * 移动动画结束时回调，会在回调了 [onMoveAnimEnd] 后回调，且只要回调了 [onMoveAnimEnd]，就一定会回调 [onMoveAnimEnd]
//   */
//  fun onMoveAnimEnd(pointerId: Int, view: View) {}
//}