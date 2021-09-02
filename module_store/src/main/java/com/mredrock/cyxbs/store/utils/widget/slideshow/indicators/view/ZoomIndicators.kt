package com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.view

import android.content.Context
import android.graphics.Path
import androidx.annotation.FloatRange
import com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.AbstractIndicatorsView
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/19
 */
class ZoomIndicators(context: Context) : AbstractIndicatorsView(context) {

    override fun onDrawMovePath(
        path1: Path,
        path2: Path,
        path3: Path,
        radius: Float,
        offsetPixels: Float,
        intervalMargin: Float
    ) {
        val zoomRadius = getZoomRadius(radius, abs(offsetPixels) / intervalMargin)
        path1.addCircle(offsetPixels, 0F, zoomRadius, Path.Direction.CCW)
    }

    private fun getZoomRadius(
        radius: Float,
        @FloatRange(from = 0.0, to = 1.0)
        ratio: Float
    ): Float {
        val multiply = 0.5F
        val k = 0.8F
        val d = 0.1F
        val x = ratio * k + d
        val increment = sin(x * PI.toFloat()) * radius * multiply
        return radius + increment
    }
}