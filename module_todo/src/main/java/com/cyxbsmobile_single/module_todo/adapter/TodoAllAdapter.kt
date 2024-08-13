import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R

/**
 * @Project: CyxbsMobile_Android
 * @File: TodoAllAdapter
 * @Author: 86199
 * @Date: 2024/8/12
 * @Description: todo模块rv的adapter
 */
class TodoAllAdapter : ListAdapter<TodoItem, TodoAllAdapter.ViewHolder>(ItemDiffCallback()) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listtext: TextView = itemView.findViewById(R.id.todo_tv_todo_title)
        val date: TextView = itemView.findViewById(R.id.todo_tv_notify_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_rv_item_todo, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {

        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("todoadapter", item.title)
        holder.date.text = item.remind_mode.notify_datetime
        holder.listtext.text = item.title
    }


    class ItemDiffCallback : DiffUtil.ItemCallback<TodoItem>() {
        override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem.todo_id == newItem.todo_id
        }

        override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem == newItem
        }


    }
}