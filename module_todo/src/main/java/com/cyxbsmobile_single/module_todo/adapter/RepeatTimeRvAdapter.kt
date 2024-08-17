package com.cyxbsmobile_single.module_todo.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R

/**
 * description:
 * author: sanhuzhen
 * date: 2024/8/16 21:38
 */
class RepeatTimeRvAdapter() :
    ListAdapter<String, RepeatTimeRvAdapter.RepeatTimeViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    /**
     * 点击按钮的回调
     */

    private var mClick: ((Int) -> Unit)? = null
    fun setOnItemClick(listener: (Int) -> Unit) {
        mClick = listener
    }


    inner class RepeatTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val repeatTimeText = itemView.findViewById<TextView>(R.id.todo_tv_repeat_time)
        val repeatTimeCancel = itemView.findViewById<ImageView>(R.id.todo_iv_repeat_time_cancel)

        init {
            repeatTimeCancel.setOnClickListener {
                mClick?.invoke(absoluteAdapterPosition)
            }
        }

        fun bind(repeatTime: String) {
            repeatTimeText.text = repeatTime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepeatTimeViewHolder {
        return RepeatTimeViewHolder(
            View.inflate(parent.context, R.layout.todo_rv_item_repeat_time_item, null)
        )
    }

    override fun onBindViewHolder(holder: RepeatTimeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}