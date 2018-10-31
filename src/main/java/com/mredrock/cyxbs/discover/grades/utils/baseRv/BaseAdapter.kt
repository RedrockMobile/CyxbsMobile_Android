package com.mredrock.cyxbs.grades.utils.baseRv

import android.content.Context
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import com.mredrock.cyxbs.common.utils.extensions.gone

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
abstract class BaseAdapter<T>(private val mContext: Context,
                              private val mData: MutableList<T>?,
                              private val mLayoutIds: IntArray) : RecyclerView.Adapter<BaseHolder>() {
    companion object {
        const val NORMAL = 0
        const val FOOTER = 2000
    }

    private var nowPosition = 0//当前位置
    private lateinit var footerTv: TextView//自定义文字
    private var isInitTv: Boolean = false

    var positionListener: ((position: Int) -> Unit)? = null
    var loadMoreListener: (() -> Unit)? = null
    var isShowFooter: Boolean = true

    abstract fun getItemType(position: Int): Int

    abstract fun onBinds(holder: BaseHolder, t: MutableList<T>?, position: Int, viewType: Int)


    override fun getItemViewType(position: Int): Int {
        nowPosition = position
        return getItemType(position)
    }

    override fun getItemCount(): Int = mData?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        return if (viewType != FOOTER) {
            BaseHolder.getHolder(mContext, parent, mLayoutIds[viewType])
        } else {
            BaseHolder.getHolder(mContext, parent, 2000)
        }
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        initListener(position)

        if (mData != null) {
            onBind(holder, mData, position)
        }

        if (getItemViewType(position) == FOOTER) {
            footerTv = (holder.itemView as BaseFooter).getTextView()
            isInitTv = true
            if (!isShowFooter)
                holder.itemView.gone()
        }
    }

    private fun initListener(position: Int){
        if (getDataSize() > 0) {
            if (position == getDataSize() - 1)
                loadMoreListener?.invoke()
        }
        positionListener?.invoke(position)
    }

    private fun onBind(holder: BaseHolder, t: MutableList<T>?, position: Int) {
        onBinds(holder, t, position, getItemViewType(position))
    }

    /**
     * 位置监听器
     */
    fun setOnPositionChangeListener(listener: ((position: Int) -> Unit)) {
        this.positionListener = listener
    }

    /**
     * 加载更多监听器 加载最后一个item时会触发监听
     */
    fun setOnLoadMoreListener(listener: (() -> Unit)) {
        this.loadMoreListener = listener
    }

    /**
     * 这里可以自定义加载的文字
     */
    fun setFooterText(text: String) {
        if (isInitTv) {
            footerTv.text = text
        }
    }

    /**
     * 获取data的大小
     */
    fun getDataSize(): Int = mData?.size ?: 0

    /**
     * 获取data
     */
    fun getData(): MutableList<T>? = mData

    /**
     * 刷新data
     */
    fun refreshData(newData: MutableList<T>) {
        mData?.let { it ->
            clearData()
            it.addAll(newData)
            notifyDataSetChanged()
        }
    }

    /**
     * 添加data
     */
    fun addData(newData: MutableList<T>) {
        mData?.let { it ->
            it.addAll(newData)
            val handler = Handler()
            val runnable = Runnable {
                notifyItemRangeInserted(it.size, newData.size)
            }
            handler.post(runnable)
        }
    }

    /**
     * 清除data
     */
    fun clearData() {
        mData?.let { it ->
            it.clear()
            notifyDataSetChanged()
        }
    }
}