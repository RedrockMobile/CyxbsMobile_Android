package com.mredrock.cyxbs.store.utils.widget.slideshow.utils

import android.content.res.Resources

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/17
 */
internal class SlideShowUtils {

    companion object {
        fun dp2Px(dp: Int): Float {
            val scale = Resources.getSystem().displayMetrics.density
            return dp * scale
        }

        fun dp2Px(dp: Float): Float {
            val scale = Resources.getSystem().displayMetrics.density
            return dp * scale
        }
    }
}