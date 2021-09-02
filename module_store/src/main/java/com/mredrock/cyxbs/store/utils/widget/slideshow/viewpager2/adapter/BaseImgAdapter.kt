package com.mredrock.cyxbs.store.utils.widget.slideshow.viewpager2.adapter

import android.content.Context
import android.util.Log
import android.util.SparseArray
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils.Refresh
import com.mredrock.cyxbs.store.utils.widget.slideshow.myinterface.OnImgRefreshListener
import com.mredrock.cyxbs.store.utils.widget.slideshow.utils.SlideShowAttrs
import com.mredrock.cyxbs.store.utils.widget.slideshow.viewpager2.pagecallback.BasePageChangeCallBack

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/27
 */
abstract class BaseImgAdapter<T> : BaseViewAdapter<ShapeableImageView>() {

    private lateinit var outerData: List<T>
    private val datas = ArrayList<T>()
    private val array = SparseArray<ConditionWithListener>()
    private var mIsCirculate = false
    private var size = 0
    private lateinit var mViewPager2: ViewPager2

    override fun getNewView(context: Context): ShapeableImageView {
        val view = ShapeableImageView(context)
        view.scaleType = ImageView.ScaleType.CENTER_INSIDE
        view.shapeAppearanceModel = ShapeAppearanceModel.builder()
            .setTopLeftCornerSize(attrs.imgLeftTopRadius)
            .setTopRightCornerSize(attrs.imgRightTopRadius)
            .setBottomLeftCornerSize(attrs.imgLeftBottomRadius)
            .setBottomRightCornerSize(attrs.imgRightBottomRadius)
            .build()
        view.setBackgroundColor(attrs.imgDefaultColor)
        return view
    }

    /**
     * **WARNING：** 因为该方法有特殊实现，所以禁止重写
     */
    @Deprecated(
        "禁止重写! ",
        ReplaceWith("onBindImageView(data, imageView, holder, position)"))
    override fun onBindViewHolder(
        view: ShapeableImageView,
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val falsePosition = BasePageChangeCallBack.getFalsePosition(mIsCirculate, position, datas.size)
        if (array.indexOfKey(falsePosition) >= 0) {
            val conditionWithListener = array[falsePosition]
            when (conditionWithListener.condition) {
                Refresh.Condition.COEXIST -> {
                    refactor(datas[falsePosition], view, holder, falsePosition)
                    conditionWithListener.l.onRefresh(view, holder, falsePosition)
                }
                Refresh.Condition.COVERED -> {
                    conditionWithListener.l.onRefresh(view, holder, falsePosition)
                }
                Refresh.Condition.ONLY_ONE -> {
                    payloads.forEach {
                        if (it == ITEM_REFRESH) {
                            array[falsePosition].l.onRefresh(view, holder, falsePosition)
                            if (array[falsePosition].condition == Refresh.Condition.ONLY_ONE) {
                                array.remove(falsePosition)
                            }
                        }
                    }
                }
            }
        }else if (payloads.isEmpty()) {
            refactor(datas[falsePosition], view, holder, falsePosition)
        }
    }

    /**
     * **WARNING：** 可以调用，但禁止重写!
     */
    override fun getItemCount(): Int {
        return size
    }

    /**
     * 因为开启了循环, 所以在外面调用 notifyDataSetChanged 时要重新计算
     */
    internal fun myNotifyDataSetChanged() {
        refreshData(outerData)
    }

    /**
     * **WARNING：** 请不要自己调用
     */
    @Deprecated("禁止自己调用! ")
    internal fun initialize(datas: List<T>, viewPager2: ViewPager2, attrs: SlideShowAttrs) {
        outerData = datas
        this.datas.addAll(datas)
        size = datas.size
        initializeAttrs(attrs)
        mViewPager2 = viewPager2
    }

    /**
     * **WARNING：** 请不要自己调用
     */
    @Deprecated("禁止自己调用! ")
    internal fun <E> refreshData(datas: List<E>) {
        outerData = datas as List<T>
        this.datas.clear()
        this.datas.addAll(datas)
        size = datas.size
        if (mIsCirculate) {
            mIsCirculate = false
            openCirculateEnabled()
        }
        notifyDataSetChanged()
    }

    /**
     * **WARNING：** 请不要自己调用
     */
    @Deprecated("禁止自己调用! ")
    internal fun openCirculateEnabled() {
        if (!mIsCirculate) {
            if (datas.size > 1) {
                mIsCirculate = true
                size = if (datas.size <= 10) 30 else datas.size * 3
            }
        }
    }

    /**
     * **WARNING：** 请不要自己调用
     */
    internal fun setImgRefreshListener(
        falsePosition: Int,
        @Refresh.Condition
        condition: Int,
        l: OnImgRefreshListener
    ) {
        array.put(falsePosition, ConditionWithListener(condition, l))
        val currentItem = mViewPager2.currentItem
        val dataSize = datas.size
        val p = currentItem - (currentItem % dataSize - falsePosition)
        repeat(5) { // 只刷新当前页面附近相同的图片, 因为其他的没有被加载, 经计算, 5 是最合适的值
            notifyItemChanged(p - 2 * dataSize + it * dataSize, ITEM_REFRESH)
        }
    }

    fun removeImgRefreshListener(position: Int) {
        return array.remove(position)
    }

    fun clearImgRefreshListener() {
        return array.clear()
    }

    /**
     * 用于设置当前 ImageView **每次进入屏幕**显示的数据(包括离开屏幕又回到屏幕)
     *
     * **WARNING:** **->> 请不要在此处创建任何新的对象 <<-**
     * 比如：设置点击监听(会生成匿名内部类)、设置只需用于 item 整个生命周期的对象等其他需要创建对象的做法,
     * ***->> 这些做法应写在 [create] 中 <<-***
     */
    abstract fun refactor(data: T, imageView: ShapeableImageView, holder: BaseViewHolder, position: Int)

    private class ConditionWithListener(
            val condition: Int,
            val l: OnImgRefreshListener
    )
}