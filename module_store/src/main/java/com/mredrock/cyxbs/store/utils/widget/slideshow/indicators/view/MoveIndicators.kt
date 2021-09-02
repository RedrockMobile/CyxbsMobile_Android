package com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.view

import android.content.Context
import android.graphics.Path
import com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.AbstractIndicatorsView

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/15
 */
class MoveIndicators(
    context: Context
) : AbstractIndicatorsView(context) {

    override fun onDrawMovePath(
        path1: Path,
        path2: Path,
        path3: Path,
        radius: Float,
        offsetPixels: Float,
        intervalMargin: Float
    ) {
        // 这只是一个简单的平移小圆点的动画
        // 其中 offsetPixels 就相当于横坐标 x
        path1.addCircle(offsetPixels, 0F, radius, Path.Direction.CCW)
    }
}