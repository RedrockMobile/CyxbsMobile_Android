package com.mredrock.cyxbs.lib.course.fragment.course.expose.fold

/**
 * 对折叠动画设置监听
 *
 * @author 985892345
 * 2023/2/18 21:22
 */
interface IFoldAnimation {
  fun doOnEnd(onEnd: () -> Unit): IFoldAnimation
  fun doOnCancel(onCancel: () -> Unit): IFoldAnimation
  fun doOnEndOrCancel(onOver: () -> Unit): IFoldAnimation
  fun doOnChange(onChanged: (weight: Float, fraction: Float) -> Unit): IFoldAnimation
}