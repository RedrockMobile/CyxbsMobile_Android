package com.mredrock.cyxbs.mine.page.feedback.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min

/**
 *@author ZhiQiang Tu
 *@time 2021/8/29  8:38
 *@signature 我们不明前路，却已在路上
 */
class SquareImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth:Int = measuredWidth
        val measuredHeight:Int = measuredHeight
        val size = min(measuredHeight,measuredWidth)
        setMeasuredDimension(size,size)
    }

}