package com.mredrock.cyxbs.todo.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.todo.R
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.todo.component.CheckLineView
import com.mredrock.cyxbs.todo.model.bean.Todo
import com.mredrock.cyxbs.todo.ui.activity.TodoDetailActivity.Companion.startActivity
import com.mredrock.cyxbs.todo.util.getColor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * description: 首页邮子清单
 * author: sanhuzhen
 * date: 2024/8/22 19:09
 */
class TodoFeedAdapter :
    ListAdapter<Todo, TodoFeedAdapter.TodoFeedViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem.todoId == newItem.todoId
            }

            override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem == newItem && oldItem.remindMode.notifyDateTime == newItem.remindMode.notifyDateTime
            }
        }
    }

    // 定义日期格式
    private val dateFormat = SimpleDateFormat("yyyy年MM月dd日HH:mm", Locale.getDefault())
    private var mClick: ((Int) -> Unit)? = null
    fun onFinishCheck(listener: (Int) -> Unit) {
        mClick = listener
    }

    inner class TodoFeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val todoTitle = itemView.findViewById<AppCompatTextView>(R.id.todo_tv_feed_title)
        private val todoFeedIv = itemView.findViewById<AppCompatImageView>(R.id.todo_iv_feed_bell)
        private val todoFeedTime =
            itemView.findViewById<AppCompatTextView>(R.id.todo_tv_feed_notify_time)
        private val icRight = itemView.findViewById<ImageView>(R.id.todo_iv_check_feed)
        private val defaultCheckbox = itemView.findViewById<CheckLineView>(R.id.todo_iv_todo_feed)

        init {
            defaultCheckbox.setOnClickListener {
                var target = 1
                defaultCheckbox.setStatusWithAnime(true){
                    // 由于自定义View的缘故，导致这里回调多次，故加个标志，防止多次回调
                    if (target == 1){
                        mClick?.invoke(absoluteAdapterPosition)
                        target++
                    }
                }
                todoTitle.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.todo_check_item_color
                    )
                )
                todoFeedTime.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.todo_check_item_color
                    )
                )
                todoFeedIv.setImageResource(R.drawable.todo_ic_addtodo_notice2)
                icRight.visible()
            }
            todoTitle.setOnClickListener {
                startActivity(getItem(absoluteAdapterPosition),itemView.context)
            }
        }

        fun bind(todo: Todo) {
            todoTitle.text = todo.title
            var endTime = todo.endTime?.replace("日", "日  ")
            if (todo.remindMode.notifyDateTime != ""){
                endTime = todo.remindMode.notifyDateTime?.replace("日", "日  ")
            }
            defaultCheckbox.apply {
                setStatusWithAnime(false)
            }
            icRight.gone()
            todoFeedIv.visible()
            todoFeedTime.visible()
            if (todo.endTime == "" && todo.remindMode.notifyDateTime == "") {
                todoFeedIv.gone()
                todoFeedTime.gone()
                updateUi(false)
            } else {
                todoFeedTime.text = endTime
                val itemTime = if (!todo.endTime.isNullOrEmpty()) {
                    try {
                        todo.endTime?.let { dateFormat.parse(it)?.time } ?: 0L
                    } catch (e: ParseException) {
                        // 如果解析失败，打印错误并使用一个默认时间值，例如当前时间
                        e.printStackTrace()
                        System.currentTimeMillis()
                    }
                } else if (!todo.remindMode.notifyDateTime.isNullOrEmpty()){
                    try {
                        todo.remindMode.notifyDateTime?.let { dateFormat.parse(it)?.time } ?: 0L
                    } catch (e: ParseException) {
                        // 如果解析失败，打印错误并使用一个默认时间值，例如当前时间
                        e.printStackTrace()
                        System.currentTimeMillis()
                    }
                } else {
                    0L
                }
                val currentTime = System.currentTimeMillis()
                updateUi(currentTime > itemTime && itemTime != 0L)
            }
        }

        private fun updateUi(isOverTime: Boolean) {
            defaultCheckbox.uncheckedColor =
                getColor(if (isOverTime) R.color.todo_check_overtime_color else R.color.todo_inner_check_eclipse_color)
            todoTitle.setTextColor(getColor(if (isOverTime) R.color.todo_text_overtime_color else com.mredrock.cyxbs.config.R.color.config_level_two_font_color))
            todoFeedTime.setTextColor(getColor(if (isOverTime) R.color.todo_textTime_overtime_color else R.color.todo_item_nf_time_color))
            todoFeedIv.setImageResource(if (isOverTime) R.drawable.todo_ic_addtodo_overtime_notice else R.drawable.todo_ic_addtodo_notice2)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoFeedViewHolder {
        return TodoFeedViewHolder(
            View.inflate(parent.context, R.layout.todo_rv_item_feed, null)
        )
    }

    override fun onBindViewHolder(holder: TodoFeedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}