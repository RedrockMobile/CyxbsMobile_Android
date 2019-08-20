package com.mredrock.cyxbs.freshman.view.widget.bubble

import kotlin.math.cos
import kotlin.math.sin

/**
 * Create by roger
 * on 2019/8/11
 */
class Bubble(var x: Double, var y: Double, val r: Double, val angle: Int) {
    fun move(distance: Double) {
        val xDelta = cos(Math.toRadians(angle.toDouble())) * distance
        val yDelta = sin(Math.toRadians(angle.toDouble())) * distance
        x += xDelta
        y -= yDelta
    }

    fun isOut(cl: Int, ct: Int, cr: Int, cb: Int): Boolean {
        if (x < cl || x > cr || y < ct || y > cb) {
            return true
        }
        return false
    }
}