package com.mredrock.cyxbs.course.page.find.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.find.room.FindPersonEntity

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/4/22 22:07
 */
class RvHistoryAdapter : ListAdapter<FindPersonEntity, RvHistoryAdapter.HistoryVH>(
  object : DiffUtil.ItemCallback<FindPersonEntity>() {
    override fun areItemsTheSame(oldItem: FindPersonEntity, newItem: FindPersonEntity): Boolean {
      return oldItem.num == newItem.num
    }

    override fun areContentsTheSame(oldItem: FindPersonEntity, newItem: FindPersonEntity): Boolean {
      return oldItem == newItem
    }

    override fun getChangePayload(oldItem: FindPersonEntity, newItem: FindPersonEntity): Any {
      return "" // 只要不为 null 就可以在刷新时去掉与缓存的互换，减少性能的消耗
    }
  }
) {

  fun setOnTextClick(l: FindPersonEntity.(Int) -> Unit): RvHistoryAdapter {
    onTextClick = l
    return this
  }

  fun setOnDeleteClick(l: FindPersonEntity.(Int) -> Unit): RvHistoryAdapter {
    onDeleteClick = l
    return this
  }
  
  fun setOnLongClick(l: FindPersonEntity.(Int) -> Unit): RvHistoryAdapter {
    mOnLongClick = l
    return this
  }

  private var onTextClick: (FindPersonEntity.(Int) -> Unit)? = null
  private var onDeleteClick: (FindPersonEntity.(Int) -> Unit)? = null
  private var mOnLongClick: (FindPersonEntity.(Int) -> Unit)? = null

  inner class HistoryVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvName: TextView = itemView.findViewById(R.id.course_tv_find_course_history_name)
    private val ibDelete: ImageButton = itemView.findViewById(R.id.course_ib_find_course_history_delete)
    init {
      tvName.setOnClickListener {
        onTextClick?.invoke(getItem(layoutPosition), layoutPosition)
      }
      tvName.setOnLongClickListener {
        mOnLongClick?.invoke(getItem(layoutPosition), layoutPosition)
        true
      }
      ibDelete.setOnClickListener {
        onDeleteClick?.invoke(getItem(layoutPosition), layoutPosition)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryVH {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.course_rv_item_find_course_history, parent, false)
    return HistoryVH(view)
  }

  override fun onBindViewHolder(holder: HistoryVH, position: Int) {
    holder.tvName.text = getItem(position).name
  }
}