package com.mredrock.cyxbs.todo.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.todo.R

/**
 * description:
 * author: sanhuzhen
 * date: 2024/8/16 21:38
 */
class RepeatTimeRvAdapter(private val position: Int) :
    ListAdapter<String, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

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
    private var mChangeRepeatTime: (() -> Unit)? = null
    fun setOnItemClick(listener: (Int) -> Unit) {
        mClick = listener
    }
    fun setOnChangeRepeatTime(listener: () -> Unit) {
        mChangeRepeatTime = listener
    }


    inner class RepeatTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val repeatTimeText = itemView.findViewById<TextView>(R.id.todo_tv_repeat_time)
        val repeatTimeCancel = itemView.findViewById<ImageView>(R.id.todo_iv_repeat_time_cancel)

        init {
            repeatTimeCancel.setOnClickListener {
                mClick?.invoke(absoluteAdapterPosition)
            }
            repeatTimeText.setOnClickListener {
                mChangeRepeatTime?.invoke()
            }
        }

        fun bind(repeatTime: String) {
            repeatTimeText.text = repeatTime
        }
    }

    inner class DetailRepeatTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDetailRepeat =
            itemView.findViewById<AppCompatTextView>(R.id.todo_detail_tv_repeat_time)
        val imgDetailCancel =
            itemView.findViewById<ImageView>(R.id.todo_detail_iv_repeat_time_cancel)

        init {
            imgDetailCancel.setOnClickListener {
                mClick?.invoke(absoluteAdapterPosition)
            }
            tvDetailRepeat.setOnClickListener {
                mChangeRepeatTime?.invoke()
            }
        }

        fun bind(repeatTime: String) {
            tvDetailRepeat.text = repeatTime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (position == 0) {
            RepeatTimeViewHolder(
                View.inflate(parent.context, R.layout.todo_rv_item_repeat_time_item, null)
            )
        } else {
            DetailRepeatTimeViewHolder(
                View.inflate(parent.context, R.layout.todo_rv_item_detail_repeat_time, null)
            )
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RepeatTimeViewHolder -> holder.bind(getItem(position))
            is DetailRepeatTimeViewHolder -> holder.bind(getItem(position))
        }
    }

}