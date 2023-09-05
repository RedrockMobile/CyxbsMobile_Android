package com.mredrock.cyxbs.noclass.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.widget
 * @ClassName:      MyFlexLayout
 * @Author:         Yan
 * @CreateDate:     2022年08月14日 21:59:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    流式布局控件 增加了高度的限制
 */
class MyFlexLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var mOnFillCallback: OnFillCallback

    companion object{
        //最大的高度限制
        private const val mMaxLine = 2
    }

    override fun generateLayoutParams(
        p: LayoutParams
    ): LayoutParams {
        return MarginLayoutParams(p)
    }

    /**
     * 设置
     */
    fun setOnFillCallback(onFillCallback: OnFillCallback) {
        mOnFillCallback = onFillCallback
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT)
    }

    /**
     * 记录行宽
     */
    private val listWidth: MutableList<Int> = ArrayList()

    /**
     * 负责设置子控件的测量模式和大小 根据所有子控件设置自己的宽和高
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 获得它的父容器为它设置的测量模式和大小
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)
        val modeWidth = MeasureSpec.getMode(widthMeasureSpec)
        val modeHeight = MeasureSpec.getMode(heightMeasureSpec)

        // 如果是warp_content情况下，记录宽和高
        var currentWidth = 0
        var currentHeight = 0

        Log.d("lx", "flex的测量 ")
        /**
         * 记录每一行的宽度，width不断取最大宽度
         */
        var lineWidth = 0

        /**
         * 每一行的高度，累加至height
         */
        var lineHeight = 0
        var lineCount = 0
        val cCount = childCount

        // 遍历每个子元素
        for (i in 0 until cCount) {
            val child: View = getChildAt(i)
            // 测量每一个child的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            // 得到child的lp
            val lp = child.layoutParams as MarginLayoutParams
            // 当前子空间实际占据的宽度
            val childWidth: Int = (child.measuredWidth + lp.leftMargin + lp.rightMargin)
            // 当前子空间实际占据的高度
            val childHeight: Int = (child.measuredHeight + lp.topMargin + lp.bottomMargin)
            /**
             * 如果加入当前child，则超出最大宽度，则的到目前最大宽度给width，类加height 然后开启新行
             */
            if (childWidth > sizeWidth){
                lineCount--
            }

            if (lineWidth + childWidth > sizeWidth) {
                listWidth.add(lineWidth)
                lineCount++
                // 取最大的
                currentWidth = lineWidth.coerceAtLeast(childWidth)
                // 重新开启新行，开始记录
                lineWidth = childWidth
                // 叠加当前高度，
                currentHeight += lineHeight
                // 开启记录下一行的高度
                lineHeight = childHeight
                // 从第0行开始的
                if (lineCount == mMaxLine) {
                    break
                }
            } else {// 否则累加值lineWidth,lineHeight取最大高度
                lineWidth += childWidth
                lineHeight = lineHeight.coerceAtLeast(childHeight)
            }
            // 如果是最后一个，则将当前记录的最大宽度和当前lineWidth做比较
            if (i == cCount - 1) {
                currentWidth = currentWidth.coerceAtLeast(lineWidth)
                currentHeight += lineHeight
            }
        }
        setMeasuredDimension(if (modeWidth == MeasureSpec.EXACTLY) sizeWidth else currentWidth,
            if (modeHeight == MeasureSpec.EXACTLY) sizeHeight else currentHeight)
    }

    /**
     * 存储所有的View，按行记录
     */
    private val mAllViews: MutableList<MutableList<View>> = ArrayList()

    /**
     * 当布局完成之后的回调
     */
    private var onLayoutCallBack : ((List<Int>) -> Unit)? = null

    /**
     * 设置布局完成之后的回调
     */
    fun setOnLayoutCallBack(onLayoutCallBack : (List<Int>) -> Unit){
        this.onLayoutCallBack = onLayoutCallBack
    }

    /**
     * 记录每一行的最大高度
     */
    private val mLineHeight: MutableList<Int> = ArrayList()


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mAllViews.clear()
        mLineHeight.clear()

        var lineWidth = 0
        var lineHeight = 0
        var lineCount = 0
        // 存储每一行所有的childView
        mAllViews.add(arrayListOf())
        // 遍历所有的子view
        for (i in 0 until childCount) {
            val child: View = getChildAt(i)
            val lp = child.layoutParams as MarginLayoutParams
            val childWidth: Int = child.measuredWidth
            val childHeight: Int = child.measuredHeight

            // 如果已经需要换行
            if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) {
                // 记录这一行所有的View以及最大高度
                mLineHeight.add(lineHeight)
                // 将当前行的childView保存，然后开启新的ArrayList保存下一行的childView
                Log.d("lx", "add - mLinesViews: = ${mAllViews.size} ")
                lineWidth = 0 // 重置行宽
                if (mAllViews.size == mMaxLine) {
                    break
                }
                lineCount ++
                mAllViews.add(arrayListOf())
            }
            /**
             * 如果不需要换行，则累加
             */
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin
            lineHeight = lineHeight.coerceAtLeast(childHeight + lp.topMargin + lp.bottomMargin)
            mAllViews[lineCount].add(child)
        }

        // 记录最后一行
        mLineHeight.add(lineHeight)
        var left = 0
        var top = 0
        // 得到总行数
        val lineNums = mAllViews.size

        for (i in 0 until lineNums) {
            // 当前行的最大高度
            lineHeight = mLineHeight[i]

            // 遍历当前行所有的View
            for (j in mAllViews[i].indices) {
                val child: View = mAllViews[i][j]
                if (View.GONE == child.visibility) { continue }
                val lp = child.layoutParams as MarginLayoutParams

                //计算childView的left,top,right,bottom
                val lc = left + lp.leftMargin
                val tc = top + lp.topMargin
                val rc: Int = lc + child.measuredWidth
                val bc: Int = tc + child.measuredHeight
                child.layout(lc, tc, rc, bc)
                left += (child.measuredWidth + lp.rightMargin + lp.leftMargin)
            }
            left = 0
            top += lineHeight
        }
        // 填充完毕一页
        if (lineNums == mMaxLine){
            var n = 0
            for (i in mAllViews.indices) {
                n += mAllViews[i].size
            }
            mOnFillCallback.onFill(n)
        }
//        设置是否居中
//        adjust()
    }

    //居中对齐
    private fun adjust() {
        var count = mAllViews.size
        if (count == 0) {
            return
        }
        if (count > mMaxLine) {
            count = mMaxLine
        }
        for (i in 0 until count) {
            val list: List<View> = mAllViews[i]
            if (list.isEmpty()) {
                return
            }
            val rightView: View = list[list.size - 1]
            val right: Int = rightView.right
            val padding = (width - right) / mMaxLine
            for (j in list.indices) {
                val view: View = list[j]

                view.layout(view.left + padding, view.top, view.right + padding, view.bottom)

            }
        }
    }

    interface OnFillCallback {
        fun onFill(itemsSize: Int)
    }

    enum class FlexState{
        COLLAPSING,//收缩状态
        EXPANDING,//展开状态
    }
}