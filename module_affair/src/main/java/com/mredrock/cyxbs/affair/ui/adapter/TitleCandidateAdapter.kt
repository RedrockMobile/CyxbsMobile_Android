package com.mredrock.cyxbs.affair.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/10 10:40
 */
class TitleCandidateAdapter : ListAdapter<String, TitleCandidateAdapter.VHolder>(
  object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
      return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
      return true
    }

    override fun getChangePayload(oldItem: String, newItem: String): Any {
      return ""
    }
  }
) {

  fun setClickListener(func: (CharSequence) -> Unit): TitleCandidateAdapter {
    mClickListener = func
    return this
  }

  private var mClickListener: ((CharSequence) -> Unit)? = null

  inner class VHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView: TextView = itemView.findViewById(R.id.affair_tv_add_affair_candidate)

    init {
      textView.setOnSingleClickListener {
        mClickListener?.invoke(textView.text)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
    val view = LayoutInflater
      .from(parent.context).inflate(R.layout.affair_rv_item_add_affair_candidate, parent, false)
    return VHolder(view)
  }

  override fun onBindViewHolder(holder: VHolder, position: Int) {
    holder.textView.text = getItem(position)
  }
}