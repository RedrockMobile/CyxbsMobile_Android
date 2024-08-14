import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
class TodoAllAdapter(private val listener: OnItemClickListener) : ListAdapter<TodoItem, TodoAllAdapter.ViewHolder>(ItemDiffCallback()) {
    class ViewHolder(itemView: View,  val listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val listtext: TextView = itemView.findViewById(R.id.todo_title_text)
        val date: TextView = itemView.findViewById(R.id.todo_notify_time)
        val deletebutton:LinearLayout=itemView.findViewById(R.id.todo_delete)
        val topbutton:LinearLayout=itemView.findViewById(R.id.todo_item_totop)
        fun bind(item: TodoItem,position: Int) {
            itemView.setOnClickListener {
                listener.onItemClick(item)
            }
            deletebutton.setOnClickListener {
                // 使用 adapterPosition 而不是 position
                val currentPosition = bindingAdapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    listener.ondeleteButtonClick(item, currentPosition)
                }
            }
            topbutton.setOnClickListener {
                // 使用 adapterPosition 而不是 position
                val currentPosition = bindingAdapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    listener.ontopButtonClick(item, currentPosition)
                }
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_rv_item_todo, parent, false)
        val viewHolder = ViewHolder(view, listener)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.date.text = item.remind_mode.notify_datetime
        holder.listtext.text = item.title
        holder.bind(item,position)
    }


    class ItemDiffCallback : DiffUtil.ItemCallback<TodoItem>() {
        override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem.todo_id == newItem.todo_id
        }

        override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem == newItem
        }


    }
    interface OnItemClickListener {
        fun onItemClick(item: TodoItem)
        fun ondeleteButtonClick(item: TodoItem,position: Int)
        fun ontopButtonClick(item: TodoItem,position: Int)
    }

}