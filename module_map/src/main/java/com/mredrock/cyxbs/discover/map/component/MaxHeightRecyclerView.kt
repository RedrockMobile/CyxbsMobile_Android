package com.mredrock.cyxbs.discover.map.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.discover.map.R


/**
 *@author zhangzhe
 *@date 2020/8/12
 *@description
 */


class MaxHeightRecyclerView : RecyclerView {
    private var mMaxHeight = 0

    constructor(context: Context?) : super(context!!)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context, attrs)
    }

    @SuppressLint("CustomViewStyleable")
    private fun initialize(context: Context, attrs: AttributeSet?) {
        val arr: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.map_max_height_recycler_view)
        mMaxHeight = arr.getLayoutDimension(R.styleable.map_max_height_recycler_view_map_max_height, mMaxHeight)
        arr.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec2 = heightMeasureSpec
        if (mMaxHeight > 0) {
            heightMeasureSpec2 = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec2 - context.dp2px(2f))
    }
}