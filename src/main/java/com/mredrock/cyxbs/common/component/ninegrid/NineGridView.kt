package com.mredrock.cyxbs.common.component.ninegrid

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.forEachChild
import org.jetbrains.anko.forEachChildWithIndex

/**
 * 九宫格布局
 * Created By jay68 on 2018/9/29.
 */
class NineGridView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : ViewGroup(context, attrs, defStyleAttr) {
    companion object {
        const val FILL = 0
        const val NORMAL = 1
    }

    private var fillType = FILL
    private var horizontalGap = 0f
    private var verticalGap = 0f
    private var ratio = 1.5f
    private var singleViewRatio = 1.6f

    private var adapter: Adapter? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (fillType == FILL && childCount in 1..4) {
            measureFillMode(widthMeasureSpec, heightMeasureSpec)
        } else {
            measureNormalMode(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private fun measureFillMode(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)

        val row = 2.takeIf { childCount > 2 } ?: 1
        val col = 2.takeIf { childCount > 1 } ?: 1

        val childWidth = (widthSize - horizontalGap * (row - 1)) / col

        when (heightSpecMode) {
            MeasureSpec.EXACTLY -> {
                setMeasuredDimension(widthSpec, heightMeasureSpec)
                if (childCount == 0) {
                    return
                }
                val heightSize = MeasureSpec.getSize(heightMeasureSpec)
                val childHeight = (heightSize - verticalGap * (col - 1)) / row

                forEachChild ({
                    val lp = it.layoutParams
                    lp.width = childWidth.toInt()
                    lp.height = childHeight.toInt()
                    val childWidthMeasureSpec = getChildMeasureSpec(widthSpec, it.paddingLeft + it.paddingRight, lp.width)
                    val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, it.paddingTop + it.paddingBottom, lp.height)
                    it.measure(childWidthMeasureSpec, childHeightMeasureSpec)
                })
            }

            MeasureSpec.AT_MOST -> {
                if (singleViewRatio > 0 && childCount == 1) {

                } else if (ratio > 0) {

                } else {

                }
            }

            MeasureSpec.UNSPECIFIED -> {
            }
        }

        if (childCount == 1) {
            val child = getChildAt(0)
            child.layoutParams.width = widthSize
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
        } else {
            forEachChildWithIndex({ i, view ->

            })
        }
    }

    private fun measureNormalMode(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var colCount = 0
        var rowCount = 0
        if (fillType == FILL && (childCount == 3 || childCount == 4)) {
            colCount = 2
            rowCount = 2
        } else {
            rowCount = (childCount / 3.0 + 0.6).toInt()
            colCount = 3.takeIf { rowCount > 1 } ?: childCount
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

    }

    fun setAdapter(adapter: Adapter) {
        if (this.adapter == adapter) {
            return
        } else if (adapter.getCount() < 0 || adapter.getCount() > 9) {
            throw IndexOutOfBoundsException("Child count must be in 0-9.[current count: ${adapter.getCount()}]")
        }
        removeAllViews()
        this.adapter = adapter
        for (i in 0 until adapter.getCount()) {
            val itemView = adapter.getItemView(i)
            addViewInLayout(itemView, i, generateDefaultLayoutParams(), true)
            itemView.setOnClickListener { adapter.onItemViewClicked(i, itemView) }
        }
        requestLayout()
    }

    override fun generateLayoutParams(attrs: AttributeSet?) = LayoutParams(context, attrs)

    override fun generateDefaultLayoutParams() = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

    override fun generateLayoutParams(p: LayoutParams?) = LayoutParams(p)

    override fun checkLayoutParams(p: LayoutParams?) = p is LayoutParams

    abstract class Adapter {
        abstract fun getItemView(position: Int): View
        abstract fun getCount(): Int

        fun onItemViewClicked(position: Int, itemView: View) = Unit
    }
}