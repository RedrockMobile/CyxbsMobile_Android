package com.mredrock.cyxbs.noclass.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.noclass.R

/**
 * 大体思路是这样的，测量和布局差不多
 * 主要还是将一个子view的上下左右表示出来，因为这样就可以复用onMeasure和onLayout了
 * 每行满了就换行，就这样，具体细节下面挨行解释。
 */
class MyFlowLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    //获取item的左右上下间隔
    private var lineSpacing = 0
    private var itemSpacing = 0

    private var maxLineCount = 3

    //获取自定义参数的
    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyFlowLayout)
        lineSpacing = typedArray.getDimensionPixelSize(R.styleable.MyFlowLayout_FlowLayout_lineSpacing,0)
        itemSpacing = typedArray.getDimensionPixelSize(R.styleable.MyFlowLayout_FlowLayout_itemSpacing,0)
        typedArray.recycle()
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val parentWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val parentHeight = MeasureSpec.getSize(heightMeasureSpec)
        val parentHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        //获取子view比较的最大宽度
        val parentMaxWidth = parentWidth - paddingRight

        var childLeft = paddingLeft
        var childRight = 0
        var childTop = paddingTop
        var childLineTop = 0   //每行最高的view，换行清空
        var childMaxRight = 0   //最右边，方便wrap_content包裹所有内容

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (child.visibility == View.GONE) {
                continue
            }

            measureChild(child, widthMeasureSpec, heightMeasureSpec)

            if (child.measuredHeight > childLineTop) {
                childLineTop = child.measuredHeight
            }

            if (childRight + child.measuredWidth + itemSpacing > parentMaxWidth) {
                childLeft = paddingLeft
                childTop += childLineTop + lineSpacing
                childLineTop = 0
            }

            childRight = childLeft + child.measuredWidth

            if (childRight > childMaxRight) {
                childMaxRight = childRight + itemSpacing
            }

            childLeft = childRight + itemSpacing
        }

        val parentBottom = childTop + childLineTop + paddingBottom
        childMaxRight += paddingRight
        val finalWidth = getMeasuredDimension(parentWidth, parentWidthMode, childMaxRight)
        val finalHeight = getMeasuredDimension(parentHeight, parentHeightMode, parentBottom)
        setMeasuredDimension(finalWidth, finalHeight)
    }

    /**
     * 确定最终的大小
     */
    private fun getMeasuredDimension(size: Int, mode: Int, childrenEdge: Int): Int {
        return when (mode) {
            MeasureSpec.EXACTLY -> size
            MeasureSpec.AT_MOST -> childrenEdge.coerceAtMost(size)
            else -> childrenEdge
        }
    }

    /**
     * 设置最大行数,传入的是预想的最大行数，本地设置的是判断的最大行数，当为+1行的时候就回调fillCallback。
     */
    fun setMaxLineCount(maxLineCount : Int){
        this.maxLineCount = maxLineCount + 1
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val parentWidth = right - left - paddingEnd

        var childLeft = paddingLeft
        var childRight: Int
        var childTop = paddingTop
        var childBottom: Int
        var childLineTop = 0

        for (i in 0 until childCount){
            val child = getChildAt(i)

            if (child.visibility == View.GONE){
                continue
            }

            if (child.measuredHeight > childLineTop){
                childLineTop = child.measuredHeight
            }

            childRight = childLeft + child.measuredWidth

            if(childRight + itemSpacing > parentWidth){
                childLeft = paddingLeft
                childTop += childLineTop + lineSpacing
                childLineTop = 0
            }

            childRight = childLeft + child.measuredWidth

            childBottom = childTop + child.measuredHeight

            child.layout(childLeft,childTop,childRight,childBottom)

            childLeft = childRight + itemSpacing
        }
    }
    interface FillCallBack{
        fun afterFill(index : Int)
    }
}