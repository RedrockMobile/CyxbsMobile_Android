package com.mredrock.cyxbs.ufield.lyt.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lyt.bean.TodoBean
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 *  author : lytMoon
 *  date : 2023/8/19 18:08
 *  description :负责展示待审核活动的Rv ListAdapter
 *  version ： 1.0
 */
class TodoRvAdapter :
    ListAdapter<TodoBean, TodoRvAdapter.RvTodoViewHolder>((RvTodoDiffCallback())) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvTodoViewHolder {
        return RvTodoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.ufield_item_rv_todo, parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RvTodoViewHolder, position: Int) {
        val itemView = getItem(position)

        holder.bind(itemView)
    }


    class RvTodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val actName: TextView = itemView.findViewById(R.id.uField_todo_activity_name)
        private val actTime: TextView = itemView.findViewById(R.id.uField_todo_activity_time)
        private val actType: TextView = itemView.findViewById(R.id.uField_todo_activity_type)
        private val actAuthor: TextView = itemView.findViewById(R.id.uField_todo_activity_author)
        private val actPhone: TextView = itemView.findViewById(R.id.uField_todo_activity_phone)


        /**
         * 进行视图的绑定
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(itemView: TodoBean) {
            actName.text = itemView.activity_title
            actTime.text = timeFormat(itemView.activity_create_timestamp)
            actType.text = itemView.activity_type
            actAuthor.text = itemView.activity_creator
            actPhone.text = itemView.activity_phone
        }


        /**
         * 加工时间戳,把时间戳转化为“年.月.日”格式
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun timeFormat(time: Int): String {
            return Instant
                .ofEpochSecond(time.toLong())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        }
    }


    class RvTodoDiffCallback : DiffUtil.ItemCallback<TodoBean>() {
        override fun areItemsTheSame(oldItem: TodoBean, newItem: TodoBean): Boolean {
            return oldItem == newItem
        }

        /**
         * 通过数据类中的一个特征值来比较
         */
        override fun areContentsTheSame(oldItem: TodoBean, newItem: TodoBean): Boolean {
            return oldItem.activity_id == newItem.activity_id
        }

    }


}

