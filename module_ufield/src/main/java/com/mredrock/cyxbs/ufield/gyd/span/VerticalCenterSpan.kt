package com.mredrock.cyxbs.ufield.gyd.span

import android.text.TextPaint
import android.text.style.MetricAffectingSpan

/**
 * description ： TODO:类的作用
 * author : 苟云东
 * email : 2191288460@qq.com
 * date : 2023/8/24 20:28
 */
class VerticalCenterSpan : MetricAffectingSpan() {

    override fun updateDrawState(paint: TextPaint?) {
        // 不对绘制状态进行修改
    }

    override fun updateMeasureState(textPaint: TextPaint) {
        textPaint?.let {
            val ascent = it.fontMetrics.ascent
            val descent = it.fontMetrics.descent
            val textHeight = descent - ascent

            val offset = (textHeight / 2) - descent
            it.baselineShift += offset.toInt()
        }
    }

}