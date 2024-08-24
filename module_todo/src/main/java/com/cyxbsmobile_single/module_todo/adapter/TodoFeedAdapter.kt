package com.cyxbsmobile_single.module_todo.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.component.CheckLineView
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.ui.activity.TodoDetailActivity
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.visible

/**
 * description:
 * author: sanhuzhen
 * date: 2024/8/22 19:09
 */
class TodoFeedAdapter :
    ListAdapter<Todo, TodoFeedAdapter.todoFeedViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem.todoId == newItem.todoId
            }

            override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem == newItem
            }
        }
    }

    private var mClick: ((Todo) -> Unit)? = null
    fun onFinishCheck(listener: (Todo) -> Unit) {
        mClick = listener
    }

    inner class todoFeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoTitle = itemView.findViewById<AppCompatTextView>(R.id.todo_tv_feed_title)
        val todoFeedIv = itemView.findViewById<AppCompatImageView>(R.id.todo_iv_feed_bell)
        val todoFeedTime = itemView.findViewById<AppCompatTextView>(R.id.todo_tv_feed_notify_time)
        val icRight = itemView.findViewById<ImageView>(R.id.todo_iv_check_feed)
        val defaultCheckbox = itemView.findViewById<CheckLineView>(R.id.todo_iv_todo_feed)

        init {
            defaultCheckbox.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION && position < currentList.size) {
                    defaultCheckbox.setStatusWithAnime(true) {
                        mClick?.invoke(currentList[position])
                    }
                    todoTitle.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.todo_check_item_color
                        )
                    )
                    icRight.visible()
                }
            }
            itemView.setOnClickListener {
                TodoDetailActivity.startActivity(getItem(absoluteAdapterPosition), itemView.context)
            }
        }

        fun bind(todo: Todo) {
            todoTitle.text = todo.title
            defaultCheckbox.setStatusWithAnime(false)
            todoTitle.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    com.mredrock.cyxbs.config.R.color.config_level_two_font_color
                )
            )
            icRight.gone()

            if (todo.remindMode.notifyDateTime == "") {
                todoFeedIv.gone()
                todoFeedTime.gone()
            } else {
                todoFeedTime.text = todo.remindMode.notifyDateTime
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): todoFeedViewHolder {
        return todoFeedViewHolder(
            View.inflate(parent.context, R.layout.todo_rv_item_feed, null)
        )
    }

    override fun onBindViewHolder(holder: todoFeedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}