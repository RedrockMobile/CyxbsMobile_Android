package com.mredrock.cyxbs.declare.pages.main.page.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.declare.R
import com.mredrock.cyxbs.declare.pages.main.bean.HomeDataBean

/**
 * ... 主页数据的adapter
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/4
 * @Description:
 */
class DeclareHomeRvAdapter : ListAdapter<HomeDataBean, DeclareHomeRvAdapter.InnerViewHolder>(
    object : DiffUtil.ItemCallback<HomeDataBean>() {
        override fun areItemsTheSame(oldItem: HomeDataBean, newItem: HomeDataBean): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: HomeDataBean, newItem: HomeDataBean): Boolean =
            oldItem.title == newItem.title

    }
) {

    private var listener: ((id: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder =
        InnerViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.declare_item_home_rv, parent, false)
        )

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        holder.titleTv.text = getItem(position).title
    }

    fun setOnItemClickedListener(listener: ((id: Int) -> Unit)) {
        this.listener = listener
    }

    inner class InnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv: TextView

        init {
            titleTv = itemView.findViewById(R.id.declare_home_item_title)
            itemView.findViewById<ConstraintLayout>(R.id.declare_home_item).setOnClickListener {
                listener?.invoke(getItem(absoluteAdapterPosition).id)
            }
        }
    }
}