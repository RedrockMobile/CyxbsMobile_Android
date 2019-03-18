package com.mredrock.cyxbs.mine.page.daily

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.PointDetail
import com.mredrock.cyxbs.mine.util.TimeUtil
import kotlinx.android.synthetic.main.mine_item_point_detail.view.*
import kotlinx.android.synthetic.main.mine_item_point_detail_header.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Create By Hosigus at 2019/3/17
 */
private const val NORMAL = 0
private const val HEADER = 20000

class PointDetailAdapter : RecyclerView.Adapter<PointDetailAdapter.BaseHolder<*>>() {

    private val list: MutableList<PointDetail> = mutableListOf()

    fun loadMore(newList: List<PointDetail>) {
        list.addAll(newList)
        notifyItemRangeInserted(list.size - newList.size, newList.size)
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BaseHolder<*>, position: Int){
        if (position != 0 && holder is NormalHolder) {
            holder.initView(list[position])
        } else {
            (holder as? HeadHolder)?.initView(BaseApp.user?.integral ?: 0)
        }
    }
    override fun getItemCount() = list.size + 1
    override fun getItemViewType(position: Int) = if (position == 0) HEADER else NORMAL
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            if (viewType == HEADER) {
                HeadHolder(inflateView(parent, R.layout.mine_item_point_detail_header))
            } else {
                NormalHolder(inflateView(parent, R.layout.mine_item_point_detail))
            }

    private fun inflateView(parent: ViewGroup, @LayoutRes resId: Int) =
            LayoutInflater.from(parent.context).inflate(resId, parent, false)

    abstract class BaseHolder<D>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun initView(data: D) = itemView.init(data)
        protected abstract fun View.init(data: D)
    }

    class HeadHolder(itemView: View) : BaseHolder<Int>(itemView) {
        override fun View.init(data: Int) {
            Glide.with(this).load(BaseApp.user!!.photoThumbnailSrc).into(civ_head)
            tv_point.text = data.toString()
        }
    }

    class NormalHolder(itemView: View) : BaseHolder<PointDetail>(itemView) {
        override fun View.init(data: PointDetail) {
            val reason = data.eventType.split(" ")
            mine_tv_reason.text = reason[0]
            if (reason.size > 1) {
                mine_tv_target.text = reason[1]
            }
            mine_tv_point.text = data.num.toString()
            mine_tv_time.text = TimeUtil.wrapTime(data.createdAt)
        }
    }
}