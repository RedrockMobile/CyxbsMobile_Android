package com.mredrock.cyxbs.qa.ui.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.appbar.AppBarLayout
import com.mredrock.cyxbs.qa.R

class ScrollCommentListBehavior: AppBarLayout.ScrollingViewBehavior {

    constructor(): super()
    constructor(context: Context, attrs: AttributeSet?) :
        super(context, attrs){
        val a = context.obtainStyledAttributes(attrs, R.styleable.ScrollingViewBehavior_Layout)
        overlayTop = a.getDimensionPixelSize(R.styleable.ScrollingViewBehavior_Layout_behavior_overlapTop, 0)
        a.recycle()
    }

    fun hide() {
//        val a = findFirstDependency(parent.getDependencies(child))
    }


}