package com.mredrock.cyxbs.ufield.lyt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lyt.bean.TodoBean
import org.w3c.dom.Text

/**
 *  author : lytMoon
 *  date : 2023/8/19 18:08
 *  description :负责展示待审核活动的Rv adapter
 *  version ： 1.0
 */
class TodoAdapter : ListAdapter<TodoBean, TodoAdapter.RvTodoViewHolder>((RvTodoDiffCallback())) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvTodoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.ufield_item_rv_todo, parent, false)
        return RvTodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RvTodoViewHolder, position: Int) {
        val itemView = getItem(position)
        holder.bind(itemView)
    }


    class RvTodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val actName = itemView.findViewById<TextView>(R.id.uField_todo_activity_name)



        /**
         * 进行视图的绑定
         */
        fun bind(itemView: TodoBean) {


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

