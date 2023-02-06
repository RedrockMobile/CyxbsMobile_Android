package com.mredrock.cyxbs.lib.course.helper.move.expose

import android.view.View

/**
 * .
 *
 * @author 985892345
 * 2023/1/29 15:06
 */
open class MoveHelper : IMove {
  
  override fun onMoveStart(view: View, x: Int, y: Int, rawX: Int, rawY: Int) {
    view.translationX = 0F
    view.translationY = 0F
  }
  
  override fun onMoving(
    view: View,
    initialX: Int,
    initialY: Int,
    initialRawX: Int,
    initialRawY: Int,
    nowX: Int,
    nowY: Int,
    nowRawX: Int,
    nowRawY: Int,
  ) {
    view.translationX = (nowRawX - initialX).toFloat()
    view.translationY = (nowRawY - initialRawY).toFloat()
  }
  
  override fun onMoveEnd(view: View, x: Int, y: Int, rawX: Int, rawY: Int) {
  
  }
}