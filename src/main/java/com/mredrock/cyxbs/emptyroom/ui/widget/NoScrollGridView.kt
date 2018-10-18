package com.mredrock.cyxbs.emptyroom.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.GridView

/**
 * Created by Cynthia on 2018/9/19
 */
class NoScrollGridView : GridView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2,
                View.MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
        val minHeight = suggestedMinimumHeight
        if (measuredHeight < minHeight) {
            val heightSpec = View.MeasureSpec.makeMeasureSpec(minHeight, View.MeasureSpec.EXACTLY)
            super.onMeasure(widthMeasureSpec, heightSpec)
        }
    }
}