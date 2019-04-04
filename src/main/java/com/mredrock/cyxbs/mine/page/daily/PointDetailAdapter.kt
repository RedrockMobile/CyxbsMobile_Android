package com.mredrock.cyxbs.mine.page.daily

import android.graphics.Color
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.PointDetail
import com.mredrock.cyxbs.mine.util.TimeUtil
import kotlinx.android.synthetic.main.mine_item_point_detail.view.*

/**
 * Create By Hosigus at 2019/3/17
 */
class PointDetailAdapter : RecyclerView.Adapter<PointDetailAdapter.BaseHolder<PointDetail>>() {

    private val darkColor = Color.parseColor("#B3000000")
    private val lightColor = Color.parseColor("#839bfa")

    var loadMoreSource: (() -> Unit)? = null

    private val list: MutableList<PointDetail> = mutableListOf()

    fun loadMore(newList: List<PointDetail>) {
        list.addAll(newList)
        notifyItemChanged(list.size - newList.size)
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (position >= list.size - 1) {
            loadMoreSource?.invoke()
        }
        return super.getItemViewType(position)
    }
    override fun onBindViewHolder(holder: BaseHolder<PointDetail>, position: Int) = holder.initView(list[position])
    override fun getItemCount() = list.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            NormalHolder(inflateView(parent, R.layout.mine_item_point_detail))

    private fun inflateView(parent: ViewGroup, @LayoutRes resId: Int) =
            LayoutInflater.from(parent.context).inflate(resId, parent, false)

    abstract class BaseHolder<D>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun initView(data: D) = itemView.init(data)
        protected abstract fun View.init(data: D)
    }

    inner class NormalHolder(itemView: View) : BaseHolder<PointDetail>(itemView) {
        override fun View.init(data: PointDetail) {
            val reason = data.eventType.split(" ")
            mine_tv_reason.text = reason[0]
            if (reason.size > 1) {
                mine_tv_target.text = reason[1]
            }
            mine_tv_point.apply {
                if (data.num.startsWith("-")) {
                    text = data.num
                    setTextColor(darkColor)
                } else {
                    val add = "+" + data.num
                    text = add
                    setTextColor(lightColor)
                }
            }
            mine_tv_time.text = TimeUtil.wrapTime(data.createdAt)
        }
    }
}