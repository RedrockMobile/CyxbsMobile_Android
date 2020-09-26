package com.mredrock.cyxbs.qa.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.forEach
import com.mredrock.cyxbs.common.utils.extensions.dip
import com.mredrock.cyxbs.common.utils.extensions.loadRedrockImage
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.common.utils.extensions.*


/**
 * 图片九宫格布局
 * Created By jay68 on 2018/9/29.
 */
class NineGridView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : FrameLayout(context, attrs, defStyleAttr) {
    companion object {
        const val MODE_FILL = 0
        const val MODE_NORMAL = 1
    }

    /**
     * 图片的排列方式
     */
    private var arrangement = MODE_NORMAL

    init {
        val typeArrayList = context.obtainStyledAttributes(attrs, intArrayOf(R.attr.arrangementMode))
        arrangement = typeArrayList.getInt(R.styleable.NineGridView_arrangementMode, MODE_NORMAL)
        typeArrayList.recycle()
    }

    /**
     * 水平方向间距
     */
    private var horizontalGap = context.dip(9).toFloat()

    /**
     * 垂直方向间距
     */
    private var verticalGap = context.dip(10).toFloat()

    /**
     * 每个子view的宽高比
     */
    private var childSizeRatio = 1f

    private var onItemClickListener: ((itemView: View, index: Int) -> Unit)? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (childCount > 9) {
            throw IllegalStateException("Only support less than 9 child.")
        } else if (childCount == 0) {
            setMeasuredDimension(widthMeasureSpec, getMeasureSpec(0))
            return
        }
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        if (arrangement == MODE_FILL) {
            measureInFillMode(parentWidth)
        } else {
            measureInNormalMode(parentWidth)
        }
    }

    private fun measureInFillMode(parentWidth: Int) {
        val widthLeft = parentWidth - paddingLeft - paddingRight
        val childWidth = when (childCount) {
            1 -> widthLeft
            2, 3, 4 -> ((widthLeft - horizontalGap) / 2).toInt()
            else -> ((widthLeft - horizontalGap * 2) / 3).toInt()
        }
        val childHeight: Int = (childWidth / childSizeRatio).toInt()
        forEach { it.measure(getMeasureSpec(childWidth), getMeasureSpec(childHeight)) }

        var parentHeight = paddingTop + paddingBottom
        parentHeight += when (childCount) {
            1, 2 -> childHeight
            3, 4, 5, 6 -> (childHeight * 2 + verticalGap).toInt()
            else -> (childHeight * 3 + verticalGap * 2).toInt()
        }
        setMeasuredDimension(getMeasureSpec(parentWidth), getMeasureSpec(parentHeight))
    }

    private fun measureInNormalMode(parentWidth: Int) {
        val widthLeft = parentWidth - paddingLeft - paddingRight
        val childWidth = ((widthLeft - horizontalGap * 2) / 3).toInt()
        val childHeight: Int = (childWidth / childSizeRatio).toInt()
        forEach { it.measure(getMeasureSpec(childWidth), getMeasureSpec(childHeight)) }

        val col = childCount / 3 + (1.takeIf { childCount % 3 != 0 } ?: 0)
        var parentHeight = paddingTop + paddingBottom
        parentHeight += (col * childHeight + (col - 1) * verticalGap).toInt()
        setMeasuredDimension(getMeasureSpec(parentWidth), getMeasureSpec(parentHeight))
    }

    private fun getMeasureSpec(size: Int) = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top = paddingTop
        var left = paddingLeft
        forEach {
            var right = left + it.measuredWidth
            var bottom = top + it.measuredHeight
            if (right - paddingLeft > measuredWidth - paddingRight) {
                //这时候应该换到下一行
                top += it.measuredHeight + verticalGap.toInt()
                left = paddingLeft
                right = left + it.measuredWidth
                bottom = top + it.measuredHeight
            }
            it.layout(left, top, right, bottom)
            left = right + horizontalGap.toInt()
        }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        child?.setOnSingleClickListener {
            for (i in 0 until childCount) {
                if (getChildAt(i) == it) {
                    onItemClickListener?.invoke(child, i)
                }
            }
        }
    }

    fun setOnItemClickListener(listener: (itemView: View, index: Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setImages(urls: List<String>) {
        repeat(childCount) {
            if (it < urls.size) {
                context.loadRedrockImage(urls[it], getChildAt(it) as ImageView)
            } else {
                removeViewAt(it)
            }
        }

        for (i in childCount until urls.size) {
            this.addView(ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                context.loadRedrockImage(urls[i], this@apply)
            }, childCount)
        }
    }
}