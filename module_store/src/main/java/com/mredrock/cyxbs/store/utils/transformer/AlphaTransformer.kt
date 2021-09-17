package com.mredrock.cyxbs.store.utils.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/8/29
 * @time 19:16
 */
class AlphaTransformer : ViewPager2.PageTransformer {

    private var minAlpha = 0.6F

    override fun transformPage(page: View, position: Float) {
        if (position < -1) { // [-Infinity, -1)
            page.alpha = minAlpha
        } else if (position <= 1) { // [-1, 1]
            if (position < 0) { // [-1, 0]
                // [1, min]
                page.alpha = minAlpha + (1 - minAlpha) * (1 + position)
            } else { // [0, 1]
                //[min, 1]
                page.alpha = minAlpha + (1 - minAlpha) * (1 - position)
            }
        } else { // (1, +Infinity]
            page.alpha = minAlpha
        }
    }
}