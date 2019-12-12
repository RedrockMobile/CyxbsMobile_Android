package com.mredrock.cyxbs.common.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.mredrock.cyxbs.common.R

/**
 * @author jon
 * @create 2019-12-12 14:51
 *
 * 描述:
 *   该自定义View用来包含不定高度，不定宽度的子项，并从左到右排列，排满一行排第二行，该自定义View采用适配器模式
 */
class RedRockAutoWarpView : FrameLayout {
    var adapter: Adapter? = null
        set(value) {
            field = value
            adapter?.context = context
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        addItemView()
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        }


    var isStrict = true


    private var spacingH: Int = 0
    private var spacingV: Int = 0


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RedRockAutoWarpView, R.attr.RedRockAutoWarpViewStyle, 0)
        spacingH = typedArray.getDimension(R.styleable.RedRockAutoWarpView_horizontalSpacing, 0f).toInt()
        spacingV = typedArray.getDimension(R.styleable.RedRockAutoWarpView_verticalsSpacing, 0f).toInt()
        isStrict = typedArray.getBoolean(R.styleable.RedRockAutoWarpView_strictMode, true)
        typedArray.recycle()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}


    private fun addItemView() {

        /**
         * 下面复用代码
         */
        fun setLayoutP(layoutParams: LayoutParams, column: Int, rowUsedHeights: MutableList<Int>, columnUsedWith: Int) {
            layoutParams.topMargin = (if (column == 0) 0 else {
                rowUsedHeights.sum() - if (columnUsedWith == 0) 0 else rowUsedHeights[column]
            }) + spacingV * (rowUsedHeights.size -
                    if (columnUsedWith == 0) 0 else 1)
            layoutParams.leftMargin = if (columnUsedWith == 0) 0 else columnUsedWith
        }

        adapter?.let { adapter ->
            var column = 0
            var rowUsedWith = 0
            val rowUsedHeights = mutableListOf<Int>()
            var i = 0
            while (i < adapter.getItemCount()) {
                val itemView = LayoutInflater.from(context).inflate(adapter.getItemId(), this, false)
                clearMagin(itemView)//清除外边距，以免影响item实际大小
                adapter.initItem(itemView, i)
                itemView.measure(MeasureSpec.makeMeasureSpec(0,
                        MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0,
                        MeasureSpec.UNSPECIFIED))
                val itemWith: Int = itemView.measuredWidth
                val itemHeight: Int = itemView.measuredHeight
                val layoutParams = itemView.layoutParams as LayoutParams

                if (measuredWidth - rowUsedWith >= itemWith + spacingH||rowUsedWith==0) {
                    setLayoutP(layoutParams, column, rowUsedHeights, rowUsedWith)
                }else {
                    column++
                    rowUsedWith = 0
                    setLayoutP(layoutParams, column, rowUsedHeights, rowUsedWith)
                }

                //若函数没有退出，说明该View可以包含子View，那么从第一行开始，记录该行
                if (rowUsedWith == 0 && column + 1 > rowUsedHeights.size) {
                    rowUsedHeights.add(itemHeight)
                } else {
                    if (rowUsedHeights[column] < itemHeight) {
                        rowUsedHeights[column] = itemHeight
                    }
                }
                //记录横向所用宽度
                rowUsedWith += itemWith + spacingH

                addView(itemView)
                i++
            }

            if (isStrict) {
                getChildAt(childCount-1).addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
                    var column1 = 0
                    var rowUsedWith1 = 0
                    val rowUsedHeights1 = mutableListOf<Int>()
                    var j = 0
                    while (j < adapter.getItemCount()) {
                        val itemView = getChildAt(j)
                        val itemWith: Int = itemView.width
                        val itemHeight: Int = itemView.height
                        val layoutParams = itemView.layoutParams as LayoutParams

                        if (measuredWidth - rowUsedWith1 >= itemWith + spacingH||rowUsedWith1==0) {
                            setLayoutP(layoutParams, column1, rowUsedHeights1, rowUsedWith1)
                        }else {
                            column1++
                            rowUsedWith1 = 0
                            setLayoutP(layoutParams, column1, rowUsedHeights1, rowUsedWith1)
                        }

                        //若函数没有退出，说明该View可以包含子View，那么从第一行开始，记录该行
                        if (rowUsedWith1 == 0 && column1 + 1 > rowUsedHeights1.size) {
                            rowUsedHeights1.add(itemHeight)
                        } else {
                            if (rowUsedHeights1[column1] < itemHeight) {
                                rowUsedHeights1[column1] = itemHeight
                            }
                        }
                        //记录横向所用宽度
                        rowUsedWith1 += itemWith + spacingH
                        itemView.layoutParams = layoutParams
                        j++
                    }
                }
            }
        }
    }



    private fun clearMagin(itemView: View?) {
        val layoutParams = itemView?.layoutParams as LayoutParams
        layoutParams.rightMargin = 0
        layoutParams.leftMargin = 0
        layoutParams.topMargin = 0
        layoutParams.bottomMargin = 0
    }

    abstract class Adapter {
        lateinit var context: Context
        abstract fun getItemId(): Int
        abstract fun getItemCount(): Int
        abstract fun initItem(item: View, position: Int)
    }
}
