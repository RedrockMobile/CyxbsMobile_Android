package com.mredrock.cyxbs.lib.course.helper.move.expose

import android.view.View

/**
 * .
 *
 * @author 985892345
 * 2023/1/29 15:08
 */
interface IMove {
  
  fun onMoveStart(view: View, x: Int, y: Int, rawX: Int, rawY: Int)
  
  fun onMoving(
    view: View,
    initialX: Int,
    initialY: Int,
    initialRawX: Int,
    initialRawY: Int,
    nowX: Int,
    nowY: Int,
    nowRawX: Int,
    nowRawY: Int,
  )
  
  fun onMoveEnd(view: View, x: Int, y: Int, rawX: Int, rawY: Int)
}