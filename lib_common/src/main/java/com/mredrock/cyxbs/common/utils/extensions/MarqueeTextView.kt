package com.mredrock.cyxbs.common.utils.extensions

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * 重写 isFocused() 实现官方的跑马灯效果
 *
 * 具体可以看这篇文章：https://cloud.tencent.com/developer/article/1591880
 */
class MarqueeTextView: AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(
            context,
            attrs
    )


    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)


    override fun isFocused(): Boolean {
        return true
    }
}
