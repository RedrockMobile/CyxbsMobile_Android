package com.mredrock.cyxbs.freshman.view.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.bean.Photo
import org.jetbrains.anko.find

/**
 * Create by roger
 * on 2019/8/5
 */
class SceneryRecyclerViewAdapter(val ctx: Context, val list: List<Photo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val header = 0
    private val common = 1
    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(mOnItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var holder: RecyclerView.ViewHolder? = null
        if (header == viewType) {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.freshman_recycle_item_scenery_header, parent, false)
            holder = HeaderViewHolder(v)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.freshman_recycle_item_scenery_common, parent, false)
            holder = CommonViewHolder(v)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            header
        } else {
            common
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CommonViewHolder -> {
                holder.title.text = list.get(position).name
                holder.photo.setImageFromUrl(list[position].photo)
                holder.itemView.setOnClickListener {
                    mOnItemClickListener?.onItemClick(position)
                }
            }
            is HeaderViewHolder -> {
                holder.title.text = list[position].name
                holder.photo.setImageFromUrl(list[position].photo)
                val styleSpan = StyleSpan(Typeface.BOLD)
                val sb = SpannableString(BaseApp.context.getString(R.string.freshman_scenery_big))
                sb.setSpan(styleSpan, 17, 21, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                holder.tvBold.text = sb

                holder.itemView.setOnClickListener {
                    mOnItemClickListener?.onItemClick(position)
                }
            }
            else -> {}
        }

    }

    class CommonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.find(R.id.tv_scenery_recycle_item_common)
        val photo: ImageView = view.find(R.id.iv_scenery_recycle_item_common)
    }
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.find(R.id.tv_scenery_recycle_item_header)
        val photo: ImageView = view.find(R.id.iv_scenery_recycle_item_header)
        val tvBold: TextView = view.find(R.id.tv_scenery_header_content)
    }
}