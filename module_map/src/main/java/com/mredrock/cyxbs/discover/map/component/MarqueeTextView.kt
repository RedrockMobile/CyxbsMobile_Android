package com.mredrock.cyxbs.discover.map.component

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * @author Jovines
 * @create 2020-02-04 7:35 PM
 *
 *
 * 描述:一个跑马灯TextView
 */
class MarqueeTextView : AppCompatTextView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(
            context,
            attrs
    )


    constructor(
            context: Context?,
            attrs: AttributeSet?,
            defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)


    override fun isFocused(): Boolean {
        return true
    }
}