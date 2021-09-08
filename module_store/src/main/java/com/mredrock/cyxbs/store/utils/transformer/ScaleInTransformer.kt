package com.mredrock.cyxbs.store.utils.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2

/**
 * ...
 * @author 985892345
 * @email 2767465918@qq.com
 * @date 2021/5/28
 */
open class ScaleInTransformer : ViewPager2.PageTransformer {

    private val center = 0.5F
    private val minScale = 0.85F

    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width
        val pageHeight = page.height

        page.pivotY = pageHeight / 2F
        page.pivotX = pageWidth / 2F
        if (position < -1) { // [-Infinity, -1)
            page.scaleX = minScale
            page.scaleY = minScale
            page.pivotX = pageWidth.toFloat()
        }else if (position <= 1) { // [-1,1]
            if (position < 0) { // [-1, 0]
                val scaleFactor = (1 + position) * (1 - minScale) + minScale
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
                page.pivotX = pageWidth * (center + center * -position)
            }else { // [1, 0]
                val scaleFactor = (1 - position) * (1 - minScale) + minScale
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
                page.pivotX = pageWidth * ((1 - position) * center)
            }
        }else { // (1, +Infinity]
            page.pivotX = 0F
            page.scaleX = minScale
            page.scaleY = minScale
        }
    }
}