package com.mredrock.cyxbs.qa.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * 用于在沉浸式状态栏时给状态栏填充高度
 *
 * 高度随便设置都可
 *
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/3/6 14:23
 */
class StatusBar(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    var mStatusBarHeight = 0
    init {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId != 0) {
            mStatusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val newHeightMS = MeasureSpec.makeMeasureSpec(mStatusBarHeight, View.MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, newHeightMS)
    }
}