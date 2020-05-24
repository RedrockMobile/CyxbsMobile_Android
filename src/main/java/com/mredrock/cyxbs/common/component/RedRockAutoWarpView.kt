package com.mredrock.cyxbs.common.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            adapter?.context = context
            adapter?.view = this
            if (adapter?.getItemCount() ?: 0 == 0) {
                return
            }
            refreshData()
            requestLayout()
        }


    /**
     * 开启严格模式，默认开启，若是不开启严格模式子项行与行之间可能无法保证正常显示，
     * 若是子项只是宽度不同，可以在属性里面关闭，可优化部分性能（有大量子项时）
     */
    private var isStrict = true

    /**
     * 最大行数，默认-1无限制
     */
    private var maxLine = -1


    private var maxColumn = -1

    /**
     * 子项的左右间距和上下间距
     */
    private var spacingH: Int = 0
    private var spacingV: Int = 0


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RedRockAutoWarpView, R.attr.RedRockAutoWarpViewStyle, 0)
        spacingH = typedArray.getDimension(R.styleable.RedRockAutoWarpView_horizontalSpacing, 0f).toInt()
        spacingV = typedArray.getDimension(R.styleable.RedRockAutoWarpView_verticalsSpacing, 0f).toInt()
        isStrict = typedArray.getBoolean(R.styleable.RedRockAutoWarpView_strictMode, true)
        maxLine = typedArray.getInteger(R.styleable.RedRockAutoWarpView_maxLine, -1)
        maxColumn = typedArray.getInteger(R.styleable.RedRockAutoWarpView_maxColumn, -1)
        typedArray.recycle()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    /**
     * 关键函数，用于将所有的子项添加到该View当中
     */
    private fun addItemView() {

        /**
         * 下面复用代码
         */
        fun setLayoutP(layoutParams: LayoutParams, column: Int, rowUsedHeights: MutableList<Int>, columnUsedWith: Int) {
            layoutParams.topMargin = (if (column == 0) 0 else {//如果当前是第一排，则为0
                //不是0则加上上面所有的行的最大高度，如果当前item是当前行第一个，这不用减去当前行，不是则减去当前行
                rowUsedHeights.sum() - if (columnUsedWith == 0) 0 else rowUsedHeights[column]
                //如果设置了上下间隔则加上间隔
            }) + spacingV * (rowUsedHeights.size - if (columnUsedWith == 0) 0 else 1)
            layoutParams.leftMargin = if (columnUsedWith == 0) 0 else columnUsedWith
        }

        adapter?.let { adapter ->
            //当前是第几排[从0计数]
            var column = 0
            //横向已经用的宽度
            var rowUsedWith = 0
            //每一行的最大高度
            val rowUsedHeights = mutableListOf<Int>()
            var i = 0
            while (i < adapter.getItemCount()) {
                val itemId = adapter.getItemId(i)
                val itemView = if (itemId != null) LayoutInflater.from(context).inflate(itemId, this, false) else adapter.getItemView(i)
                itemView ?: throw NullPointerException("item为空，请检查是否复写getItemId或者getItemView中的其中一个")
                clearMargin(itemView)//清除外边距，以免影响item实际大小
                adapter.initItem(itemView, i)
                itemView.measure(MeasureSpec.makeMeasureSpec(0,
                        MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0,
                        MeasureSpec.UNSPECIFIED))
                val itemWith: Int = itemView.measuredWidth
                val itemHeight: Int = itemView.measuredHeight
                val layoutParams = itemView.layoutParams as LayoutParams

                if (measuredWidth - rowUsedWith >= itemWith || rowUsedWith == 0) {
                    setLayoutP(layoutParams, column, rowUsedHeights, rowUsedWith)
                } else {
                    column++

                    //如果maxLine等于-1则默认包含所有子项。
                    if (maxLine != -1 && column == maxLine) {
                        break
                    }
                    rowUsedWith = 0
                    setLayoutP(layoutParams, column, rowUsedHeights, rowUsedWith)
                }

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
                getChildAt(childCount - 1).addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                    var column1 = 0
                    var rowUsedWith1 = 0
                    val rowUsedHeights1 = mutableListOf<Int>()
                    var j = 0
                    while (j < adapter.getItemCount()) {
                        val itemView = getChildAt(j)
                        j++
                        itemView ?: continue
                        val itemWith: Int = itemView.width
                        val itemHeight: Int = itemView.height
                        val layoutParams = itemView.layoutParams as LayoutParams

                        if (measuredWidth - rowUsedWith1 >= itemWith + spacingH || rowUsedWith1 == 0) {
                            setLayoutP(layoutParams, column1, rowUsedHeights1, rowUsedWith1)
                        } else {
                            column1++
                            if (maxLine != -1 && column == maxLine) {
                                break
                            }
                            rowUsedWith1 = 0
                            setLayoutP(layoutParams, column1, rowUsedHeights1, rowUsedWith1)
                        }

                        //从第一行开始，记录
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

                    }
                }
            }
        }
    }

    /**
     * 数据发生更改，刷新数据
     */
    fun refreshData() {
        removeAllViews()
        if (adapter?.getItemCount() != 0) {
            addItemView()
        }
    }

    /**
     * 清除view的外边距
     */
    private fun clearMargin(itemView: View) {
        val layoutParams = if (itemView.layoutParams != null) itemView.layoutParams as LayoutParams
        else {
            LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                itemView.layoutParams = this
            }
        }
        layoutParams.rightMargin = 0
        layoutParams.leftMargin = 0
        layoutParams.topMargin = 0
        layoutParams.bottomMargin = 0
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
            return if (getItemId() == null) {
                null
            } else getItemId()
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
