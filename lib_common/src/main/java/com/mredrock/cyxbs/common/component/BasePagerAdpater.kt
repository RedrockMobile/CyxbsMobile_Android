package com.mredrock.cyxbs.common.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

abstract class BasePagerAdapter<V : View, D>(var list: List<D> = listOf()) : PagerAdapter() {

    fun refresh(list: List<D>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getCount() = list.size

    override fun isViewFromObject(view: View, `object`: Any) = view === `object`

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as View)

    override fun getItemPosition(`object`: Any) = POSITION_NONE

    @Suppress("UNCHECKED_CAST")
    override fun instantiateItem(container: ViewGroup, position: Int): V =
            (getLayoutId()?.let { LayoutInflater.from(container.context).inflate(it, container, false) as? V }
                    ?: createView(container.context)
                    ?: throw Exception("Can't create View,Please override getLayoutId() or createView()"))
                    .apply {
                        initView(list[position], position)
                        container.addView(this)
                    }


    protected open fun getLayoutId(): Int? = null

    protected open fun createView(context: Context): V? = null

    abstract fun V.initView(mData: D, mPos: Int)

}