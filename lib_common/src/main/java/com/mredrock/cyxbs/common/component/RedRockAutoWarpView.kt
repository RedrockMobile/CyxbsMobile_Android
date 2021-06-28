package com.mredrock.cyxbs.common.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.mredrock.cyxbs.common.R

/**
 * @author Jovines
 * @create 2019-12-12 14:51
 *
 * 描述:
 *   该自定义View用来包含不定高度，不定宽度的子项，并从左到右排列，排满一行排第二行，该自定义View采用适配器模式
 *
 * 注意：不要给子item设置的最外层设置外边距，因为设置了也没有用
 *      我留有属性来设置子项的横向距离和纵向距离具体请看该自定义View的属性配置xml文件
 */
class RedRockAutoWarpView : FrameLayout {

    var adapter: Adapter? = null
        set(value) {
            field = value
            value?.let { adapter ->
                adapter.context = context
                adapter.view = this
                refreshData()
            }
        }


    /**
     * 最大行数，默认-1无限制
     */
    private var maxLine = -1


    /**
     * 子项的左右间距和上下间距
     */
    private var spacingH: Int = 0

    private var spacingV: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.RedRockAutoWarpView, R.attr.RedRockAutoWarpViewStyle, 0
        )
        spacingH =
                typedArray.getDimension(R.styleable.RedRockAutoWarpView_horizontalSpacing, 0f).toInt()
        spacingV =
                typedArray.getDimension(R.styleable.RedRockAutoWarpView_verticalsSpacing, 0f).toInt()
        maxLine = typedArray.getInteger(R.styleable.RedRockAutoWarpView_maxLine, -1)
        typedArray.recycle()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    )

    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    //每一行的最大高度
    val measureColumnUsedHeights = mutableListOf<Int>()


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (adapter == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            val mWidth = MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd
            //每一行的最大高度
            adapter?.let {
                //当前是第几排[从0计数]
                var row = 0
                //横向已经用的宽度
                var rowUsedWidth = 0
                var i = 0
                while (i < childCount) {
                    val itemView = getChildAt(i)
                    itemView.measure(
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    )
                    val itemWidth: Int = itemView.measuredWidth
                    val itemHeight: Int = itemView.measuredHeight
                    val remainingWidth = mWidth - rowUsedWidth


                    if (itemWidth >= remainingWidth && rowUsedWidth != 0) {
                        row++
                        if (row == maxLine) break
                        rowUsedWidth = 0//如果超过设定的行宽，就需要换行，正在使用的行宽就直接清零
                        continue
                    }

                    if (rowUsedWidth == 0 && row + 1 > measureColumnUsedHeights.size) {
                        measureColumnUsedHeights.add(itemHeight)
                    } else {
                        if (measureColumnUsedHeights[row] < itemHeight) {
                            measureColumnUsedHeights[row] = itemHeight
                        }
                    }
                    val realWidth = if (itemWidth > MeasureSpec.getSize(widthMeasureSpec)) {
                        MeasureSpec.getSize(widthMeasureSpec)
                    } else {
                        itemWidth
                    }

                    //记录横向所用宽度
                    rowUsedWidth += realWidth + spacingH
                    i++
                }
            }
            setMeasuredDimension(
                    MeasureSpec.getSize(widthMeasureSpec),
                    measureColumnUsedHeights.sum() + spacingV * (measureColumnUsedHeights.count() - 1) + paddingTop + paddingBottom
            )
        }

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (adapter == null) {
            super.onLayout(changed, left, top, right, bottom)
        } else {
            adapter?.let {
                //当前是第几排[从0计数]
                var row = 0
                //横向已经用的宽度
                var rowUsedWidth = 0
                var columnUsedHeight = 0
                //每一行的最大高度
                val columnUsedHeights = mutableListOf<Int>()
                val horizontalPadding = paddingStart + paddingEnd
                val availableWidth = measuredWidth - horizontalPadding
                var i = 0
                while (i < childCount) {
                    val itemView = getChildAt(i)
                    val itemWidth: Int = itemView.measuredWidth
                    val itemHeight: Int = itemView.measuredHeight
                    val remainingWidth = availableWidth - rowUsedWidth
                    var realWidth: Int

                    if (itemWidth >= remainingWidth && rowUsedWidth != 0) {
                        row++
                        if (row == maxLine) break
                        columnUsedHeight =
                                columnUsedHeights.sum() + spacingV * columnUsedHeights.count()
                        rowUsedWidth = 0
                        continue
                    } else {
                        realWidth = if (itemWidth > availableWidth) {
                            availableWidth
                        } else {
                            itemWidth
                        }

                        val centerTop = ((measureColumnUsedHeights.getOrElse(row) { 0 } - itemHeight) / 2).run {
                            if (this >= 0)
                                this
                            else 0
                        }
                        itemView.layout(
                                paddingStart + rowUsedWidth,
                                centerTop + paddingTop + columnUsedHeight,
                                paddingStart + rowUsedWidth + realWidth,
                                centerTop + paddingTop + columnUsedHeight + itemHeight
                        )
                    }

                    if (rowUsedWidth == 0 && row + 1 > columnUsedHeights.size) {
                        columnUsedHeights.add(itemHeight)
                    } else {
                        if (columnUsedHeights[row] < itemHeight) {
                            columnUsedHeights[row] = itemHeight
                        }
                    }

                    //记录横向所用宽度
                    rowUsedWidth += realWidth + spacingH
                    i++
                }
            }

        }
    }

    fun refreshData() {
        removeAllViews()
        val itemCount = adapter?.getItemCount() ?: 0
        if (itemCount != 0) {
            adapter?.let { addItemView(itemCount, it) }
        }
    }

    private fun addItemView(
            count: Int,
            adapter: Adapter
    ) {
        for (i in 0 until count) {
            val itemId = adapter.getItemId(i)
            val itemView = if (itemId != null)
                LayoutInflater.from(context).inflate(itemId, this, false)
            else adapter.getItemView(i)
            itemView?.let { adapter.initItem(it, i) }
            addView(itemView)
        }
    }

    /**
     * 适配器模式需要设置的适配器，需要使用这个view必需传入该Adapter的子类对象
     */
    abstract class Adapter {
        /**
         * 这两个变量可以在下面任意一个复写对的方法中使用，
         * 但是不可以在构造器或者init中使用
         */
        lateinit var context: Context
        lateinit var view: RedRockAutoWarpView

        //下面三个方法都可以用来获取item，任写一个就可以了
        open fun getItemId(): Int? {
            return null
        }

        open fun getItemId(position: Int): Int? {
            return getItemId()
        }

        open fun getItemView(position: Int): View? = null

        /**
         * 这个方法用于初始化view
         * @param item 对应位置上的view
         * @param position 位置
         */
        open fun initItem(item: View, position: Int) {}

        /**
         * @return 有多少子项
         */
        abstract fun getItemCount(): Int

    }
}
