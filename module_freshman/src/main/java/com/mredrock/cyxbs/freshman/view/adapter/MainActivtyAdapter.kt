package com.mredrock.cyxbs.freshman.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.bean.FreshTextItem
import org.jetbrains.anko.find

class FreshAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private  var mOnItemClickListener: OnItemClickListener? = null
    private val ITEM_VIEW_TYPE_HEADER = 0
    private val ITEM_VIEW_TYPE_ITEM = 1
    private val ITEM_VIEW_TYPE_FOOTER = 2

    fun setOnItemClickListener(mOnItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener
    }

    private fun getString(id: Int): String {
        return BaseApp.context.resources.getString(id)
    }

    private val data = listOf<FreshTextItem>(
        FreshTextItem(getString(R.string.freshman_main_essential), getString(R.string.freshman_main_essential_detail)),
        FreshTextItem(getString(R.string.freshman_main_zhilu), getString(R.string.freshman_main_zhilu_detail)),
        FreshTextItem(getString(R.string.freshman_main_shedule), getString(R.string.freshman_main_shedule_detail)),
        FreshTextItem(getString(R.string.freshman_main_xiaoyuanzhiyin), getString(R.string.freshman_main_xiaoyuanzhiyin_detail)),
        FreshTextItem(getString(R.string.freshman_main_online), getString(R.string.freshman_main_online_detail)),
        FreshTextItem(getString(R.string.freshman_main_more), getString(R.string.freshman_main_more_detail)),
        FreshTextItem(getString(R.string.freshman_main_about), getString(R.string.freshman_main_about_detail))
        )

    class TextViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.find(R.id.tv_title)
        val discriptView: TextView = view.find(R.id.tv_discript)
        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.freshman_recycle_item_main_text_item, parent, false)
                return TextViewHolder(view)
            }
        }

    }
    class HeaderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.freshman_recycle_item_main_header, parent, false)
                return HeaderViewHolder(view)
            }
        }
    }
    class FooterViewHolder(view: View): RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): FooterViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.freshman_recycle_item_main_footer, parent, false)
                return FooterViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> TextViewHolder.from(
                parent
            )
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(
                parent
            )
            ITEM_VIEW_TYPE_FOOTER -> FooterViewHolder.from(
                parent
            )
            else -> throw ClassCastException("unknown type of viewholder")
        }
    }

    override fun getItemCount(): Int {
        return data.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TextViewHolder -> {
                val item = data[position - 1]
                holder.discriptView.text = item.discript
                holder.titleView.text = item.title
                holder.itemView.setOnClickListener (View.OnClickListener {
                    mOnItemClickListener?.onItemClick(position)
                })
            }
            is HeaderViewHolder -> {}
            is FooterViewHolder -> {}
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ITEM_VIEW_TYPE_HEADER
            //8 -> ITEM_VIEW_TYPE_FOOTER
            else -> ITEM_VIEW_TYPE_ITEM
        }
    }

}
interface OnItemClickListener {
    fun onItemClick(position: Int)
}