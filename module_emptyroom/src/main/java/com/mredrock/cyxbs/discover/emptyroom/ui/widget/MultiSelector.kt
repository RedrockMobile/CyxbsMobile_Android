package com.mredrock.cyxbs.discover.emptyroom.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.mredrock.cyxbs.discover.emptyroom.R
import com.mredrock.cyxbs.discover.emptyroom.utils.ViewInitializer
import kotlinx.android.synthetic.main.emptyroom_layout_multi_selector.view.*
import java.util.*

/**
 * Created by Cynthia on 2018/9/18
 */
class MultiSelector : FrameLayout {

    val NO_LIMIT = context.resources.getInteger(R.integer.emptyroom_no_limit)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        init(attributeSet)
    }

    private var mDisplayValues: MutableList<*> = ArrayList<Any>()
    private var mValues: MutableList<Int> = ArrayList()
    private val mSelectedNumbers = HashSet<Int>()

    private var mMaxSelectedNum: Int = 0
    private var mMinSelectedNum: Int = 0

    private var mInitializer: ViewInitializer? = null
    private lateinit var mListener: OnItemSelectedChangeListener

    private fun init(attributeSet: AttributeSet?) {
        val array = context.obtainStyledAttributes(attributeSet, R.styleable.MultiSelector)
        mMaxSelectedNum = array.getInteger(R.styleable.MultiSelector_max_num, NO_LIMIT)
        mMinSelectedNum = array.getInteger(R.styleable.MultiSelector_min_num, NO_LIMIT)
        val radioButtonMode = array.getBoolean(R.styleable.MultiSelector_radio_button_mode, false)
        val valuesId = array.getResourceId(R.styleable.MultiSelector_values, -1)
        val displayValuesId = array.getResourceId(R.styleable.MultiSelector_display_values, -1)
        array.recycle()

        if (radioButtonMode) {
            mMaxSelectedNum = 1
            mMinSelectedNum = mMaxSelectedNum
        }
        if (valuesId != -1) {
            setValues(resources.getIntArray(valuesId))
        }
        if (displayValuesId != -1) {
            setDisplayValues(resources.getStringArray(displayValuesId))
        }

        inflate()
    }

    private fun inflate() {
        LayoutInflater.from(context).inflate(R.layout.emptyroom_layout_multi_selector, this)
    }

    fun setValues(values: IntArray) {
        mValues.clear()
        val list = ArrayList<Int>()
        for (i in values) {
            list.add(i)
        }
        setValues(list)
    }

    private fun setValues(values: MutableList<Int>) {
        mValues = values
    }

    fun <T> setDisplayValues(displayValues: Array<T>) {
        mDisplayValues.clear()
        setDisplayValues(Arrays.asList(*displayValues))
    }

    fun <T> setDisplayValues(displayValues: MutableList<T>) {
        mDisplayValues = displayValues
        if (mInitializer != null) {
            mInitializer?.getAdapter()?.notifyDataSetChanged()
        }
    }

    fun setSelected(position: Int, selected: Boolean): Boolean {
        var changed = false

        if (isRadioButtonMode() && !isPositionSelected(position)) {
            mSelectedNumbers.clear()
            mSelectedNumbers.add(position)
            changed = true
        } else if (allowChange(position, selected)) {
            if (selected) {
                mSelectedNumbers.add(position)
            } else {
                mSelectedNumbers.remove(position)
            }
            changed = true
        }

        if (changed && mInitializer != null) {
            scrollToPosition(position)
            mInitializer?.getAdapter()?.notifyDataSetChanged()
        }
        return changed
    }

    private fun scrollToPosition(position: Int) {
        if (mInitializer != null) {
            val count = mInitializer!!.getAdapter().itemCount
            val pos = if (position >= count) count else position
            rv.scrollToPosition(pos)
        }
    }

    fun toggle(position: Int): Boolean {
        return setSelected(position, !isPositionSelected(position))
    }

    private fun allowChange(position: Int, selected: Boolean): Boolean {
        val size = selectedSize()
        //选中状态与当前状态一致时不改变
        if (selected == isPositionSelected(position)) return false

        return if (!selected && size > mMinSelectedNum)
            true   //选中数量不允许小于最小值
        else if (selected && mMaxSelectedNum == NO_LIMIT)
            true  //选中数量不允许大于最大值
        else selected && size < mMaxSelectedNum
    }

    fun clearSelected() {
        mSelectedNumbers.clear()
    }

    fun setViewInitializer(initializer: ViewInitializer) {
        mInitializer = initializer
        rv.layoutManager = mInitializer?.getLayoutManager()
        rv.adapter = mInitializer?.getAdapter()
        if (initializer.getItemDecoration() != null) {
            rv.addItemDecoration(initializer.getItemDecoration()!!)
        }
        if (selectedSize() > 0) {
            scrollToPosition(mSelectedNumbers.toTypedArray()[0])
        }
    }

    fun setOnItemSelectedChangeListener(listener: OnItemSelectedChangeListener) {
        mListener = listener
    }

    fun setMaxSelectedNum(maxSelectedNum: Int) {
        mMaxSelectedNum = maxSelectedNum
    }

    fun setMinSelectedNum(minSelectedNum: Int) {
        mMinSelectedNum = minSelectedNum
    }

    fun <T> getDisplayValues(): MutableList<T> {
        return mDisplayValues as MutableList<T>
    }

    fun <T> getDisplayValue(position: Int): T? {
        var value: T? = null
        if (position >= 0 && position < mDisplayValues.size) {
            value = mDisplayValues[position] as T
        }
        return value
    }

    fun getValues(): List<Int> {
        return mValues
    }

    fun getValue(position: Int): Int {
        var value = position
        if (position >= 0 && position < mValues.size) {
            value = mValues[position]
        }
        return value
    }

    fun getSelectedValues(): IntArray {
        val values = IntArray(mSelectedNumbers.size)
        val iterator = mSelectedNumbers.iterator()
        for (i in mSelectedNumbers.indices) {
            values[i] = getValue(iterator.next())
        }
        return values
    }

    fun getMaxSelectedNum(): Int {
        return mMaxSelectedNum
    }

    fun getMinSelectedNum(): Int {
        return mMinSelectedNum
    }

    fun selectedSize(): Int {
        return mSelectedNumbers.size
    }

    fun isPositionSelected(position: Int): Boolean {
        return mSelectedNumbers.contains(position)
    }

    fun isValueSelected(values: Int): Boolean {
        return isPositionSelected(mValues.indexOf(values))
    }

    fun <T> isDisplayValueSelected(displayValues: T): Boolean {
        return isPositionSelected(mDisplayValues.indexOf(displayValues))
    }

    private fun isRadioButtonMode(): Boolean {
        return mMaxSelectedNum == mMinSelectedNum && mMaxSelectedNum == 1
    }

    abstract class Adapter<T, VH : androidx.recyclerview.widget.RecyclerView.ViewHolder>(protected val mSelector: MultiSelector) : androidx.recyclerview.widget.RecyclerView.Adapter<VH>() {

        protected abstract fun bindView(holder: VH, displayValue: T, selected: Boolean, position: Int)

        override fun onBindViewHolder(holder: VH, position: Int) {
            bindView(holder, mSelector.getDisplayValue<Any>(position) as T,
                    mSelector.isPositionSelected(position), position)

            bindClickListener(holder, position)
        }

        private fun bindClickListener(holder: VH, position: Int) {
            holder.itemView.setOnClickListener {
                val listener = mSelector.mListener
                listener.onItemClickListener()
                if (mSelector.toggle(position)) {
                    performSelectedChange(position, holder)
                }
            }
        }

        private fun performSelectedChange(position: Int, holder: VH) {
            val listener = mSelector.mListener
            val value = mSelector.getValue(position)
            val selected = mSelector.isPositionSelected(position)
            listener.onItemSelectedChange()

        }

        override fun getItemCount(): Int {
            return mSelector.mDisplayValues.size
        }
    }
}

interface OnItemSelectedChangeListener {
    fun onItemClickListener()

    fun onItemSelectedChange()
}