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
import java.util.Collections

/**
 * @Project: CyxbsMobile_Android
 * @File: TodoAllAdapter
 * @Author: 86199
 * @Date: 2024/8/12
 * @Description: todo模块rv的adapter
 */

class TodoAllAdapter(private val listener: OnItemClickListener) :
    ListAdapter<TodoItem, TodoAllAdapter.ViewHolder>(ItemDiffCallback()), ItemTouchHelperAdapter {

    var pinnedItems: MutableList<TodoItem> = mutableListOf()
    var isEnabled = false

    fun updateEnabled(enabled: Boolean) {
        isEnabled = enabled
        Log.d("TodoAllAdapter", "setEnabled called with: $isEnabled")
        notifyItemRangeChanged(0, itemCount)    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = if (viewType == VIEW_TYPE_PINNED) {
            R.layout.todo_rv_item_todo_pinned
        } else if (!isEnabled) {
            R.layout.todo_rv_item_todo
        } else {
            R.layout.todo_rv_item_manage
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (pinnedItems.contains(item)) {
            VIEW_TYPE_PINNED
        } else {
            VIEW_TYPE_DEFAULT
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val currentList = currentList.toMutableList()

        // 如果置顶的项参与交换，则不进行交换
        if (pinnedItems.contains(currentList[fromPosition]) || pinnedItems.contains(currentList[toPosition])) {
            return
        }

        Collections.swap(currentList, fromPosition, toPosition)
        submitList(currentList)
    }
    public override fun getItem(position: Int): TodoItem {
        return super.getItem(position)
    }
    inner class ViewHolder(itemView: View, private val listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        private val listtext: TextView = itemView.findViewById(R.id.todo_title_text)
        private val date: TextView = itemView.findViewById(R.id.todo_notify_time)
        private val deletebutton: LinearLayout = itemView.findViewById(R.id.todo_delete)
        private val topbutton: LinearLayout = itemView.findViewById(R.id.todo_item_totop)

        fun bind(item: TodoItem) {
            listtext.text = item.title
            date.text = item.remind_mode.notify_datetime

            itemView.setOnClickListener {
                listener.onItemClick(item)
            }
            deletebutton.setOnClickListener {
                val currentPosition = bindingAdapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    listener.ondeleteButtonClick(item, currentPosition)
                }
            }
            topbutton.setOnClickListener {
                val currentPosition = bindingAdapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    // 如果当前项已置顶，点击后取消置顶，否则置顶
                    if (pinnedItems.contains(item)) {
                        pinnedItems.remove(item)
                    } else {
                        pinnedItems.add(item)
                    }
                    submitList(currentList.toList()) // 确保整个列表被刷新
                    listener.ontopButtonClick(item, currentPosition)
                }
            }

            // 如果当前项是置顶项，则隐藏置顶按钮
            topbutton.visibility = if (pinnedItems.contains(item)) View.GONE else View.VISIBLE
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<TodoItem>() {
        override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: TodoItem)
        fun ondeleteButtonClick(item: TodoItem, position: Int)
        fun ontopButtonClick(item: TodoItem, position: Int)
    }

    companion object {
        private const val VIEW_TYPE_PINNED = 1
        private const val VIEW_TYPE_DEFAULT = 0
    }
}
