import android.annotation.SuppressLint
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.component.CheckLineView
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Locale

/**
 * @Project: CyxbsMobile_Android
 * @File: TodoAllAdapter
 * @Author: 86199
 * @Date: 2024/8/12
 * @Description: todo模块rv的adapter
 */

class TodoAllAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Todo, TodoAllAdapter.ViewHolder>(ItemDiffCallback()), ItemTouchHelperAdapter {

    var isEnabled = false
    // 定义日期格式
    private val dateFormat = SimpleDateFormat("yyyy年MM月dd日HH:mm", Locale.getDefault())
    var selectItems:MutableList<Todo> = mutableListOf()



    fun updateEnabled(enabled: Boolean) {
        val oldEnabled = isEnabled
        isEnabled = enabled

        // 如果状态没有变化，则无需刷新
        if (oldEnabled == isEnabled) return

        // 如果启用状态，刷新所有项的选择状态
        if (isEnabled) {
            notifyItemRangeChanged(0, itemCount)
        } else {
            // 退出 manage 状态时，重置所有复选框的选中状态
            selectItems.clear()
            notifyItemRangeChanged(0, itemCount)
        }
        Log.d("TodoAllAdapter", "setEnabled called with: $isEnabled")
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = when (viewType) {
            VIEW_TYPE_MANAGE -> R.layout.todo_rv_item_manage
            VIEW_TYPE_PINNED -> R.layout.todo_rv_item_todo_pinned
            VIEW_TYPE_DEFAULT -> R.layout.todo_rv_item_todo
            VIEW_TYPE_DELAY ->R.layout.todo_rv_item_delay
            else -> throw IllegalArgumentException("Unknown view type")
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view, listener,viewType)
    }

    fun deleteSelectedItems() {
        val currentList = currentList.toMutableList()
        // 遍历当前列表，从后往前删除选中的项
        for (i in itemCount - 1 downTo 0) {
            if (selectItems.contains(currentList[i])) {
//                selectItems.removeAt(i)
                currentList.removeAt(i)

            }
        }
        submitList(currentList)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun selectedall(){
        // 清空当前的选中项
        selectItems.clear()
        // 将所有项添加到 selectItems 中
        selectItems.addAll(currentList)
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun toSelectedall(){
        // 清空当前的状态
        // 清空当前的选中项
        selectItems.clear()
        notifyDataSetChanged()
    }
    fun topSelectedItems() {
        val currentList = currentList.toMutableList()
        val selectedItems = selectItems

        for (item in selectedItems) {
            item.isPinned = 1
        }
        // 从 currentList 中移除选中的项
        currentList.removeAll(selectedItems)

        // 将选中的项添加到 currentList 的顶部
        currentList.addAll(0, selectedItems)

        // 提交更新后的列表并刷新 RecyclerView
        submitList(currentList){
            notifyDataSetChanged()
        }
        // 清空选中状态
        selectItems.clear()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val isSelected = selectItems.contains(item)
        Log.d("TodoAllAdapter", "onBindViewHolder - Position: $position, isEnabled: $isEnabled, isSelected: $isSelected")
        if (isEnabled) {
            holder.checkbox?.isChecked = isSelected
            holder.checkbox?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectItems.add(item)
                } else {
                    selectItems.remove(item)
                }
            }

        } else {
           holder.defaultcheckbox
        }
        holder.bind(item)
    }


    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)

        // 检查 notifyDateTime 是否为空或空字符串
        val itemTime = if (!item.remindMode.notifyDateTime.isNullOrEmpty()) {
            try {
                dateFormat.parse(item.remindMode.notifyDateTime)?.time ?: 0L
            } catch (e: ParseException) {
                // 如果解析失败，打印错误并使用一个默认时间值，例如当前时间
                e.printStackTrace()
                System.currentTimeMillis()
            }
        } else {
            0L
        }

        val currentTime = System.currentTimeMillis()

        return if (currentTime > itemTime) {
            VIEW_TYPE_DELAY
        } else {
            when {
                item.isPinned == 1 -> VIEW_TYPE_PINNED
                isEnabled -> VIEW_TYPE_MANAGE
                else -> VIEW_TYPE_DEFAULT
            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val currentList = currentList.toMutableList()

        // 如果置顶的项参与交换，则不进行交换
        if (currentList[fromPosition].isPinned==1 || currentList[toPosition].isPinned==1) {
            return
        }
        Collections.swap(currentList, fromPosition, toPosition)
        submitList(currentList)
    }

    public override fun getItem(position: Int): Todo {
        return super.getItem(position)
    }

    inner class ViewHolder(itemView: View, private val listener: OnItemClickListener, private val viewType: Int) :
        RecyclerView.ViewHolder(itemView) {

        val listtext: TextView = itemView.findViewById(R.id.todo_title_text)
        private val date: TextView = itemView.findViewById(R.id.todo_notify_time)
        private val deletebutton: LinearLayout = itemView.findViewById(R.id.todo_delete)
        private val topbutton: LinearLayout = itemView.findViewById(R.id.todo_item_totop)
        private val icRight:ImageView?=itemView.findViewById(R.id.todo_iv_check)
        val defaultcheckbox: CheckLineView? = if (!isEnabled) {
            itemView.findViewById(R.id.todo_item_check) as? CheckLineView
        } else {
            null
        }
        val checkbox: CheckBox? = if ( isEnabled) {
            itemView.findViewById(R.id.todo_item_check) as? CheckBox
        } else {
            null
        }

        fun bind(item: Todo) {
            listtext.text = item.title
            date.text = item.remindMode.notifyDateTime
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
                    listener.ontopButtonClick(item, currentPosition)
                }
            }
            if(!isEnabled) {
                defaultcheckbox?.setOnClickListener {
                    defaultcheckbox.setStatusWithAnime(true)
                    listtext.setTextColor(ContextCompat.getColor(itemView.context, R.color.todo_check_item_color))
                    icRight?.let {
                        it.visibility = View.VISIBLE
                    }
                    // 禁用点击事件
                    defaultcheckbox.setOnClickListener(null)
                }
            }

            // 如果当前项是置顶项，则隐藏置顶按钮
            topbutton.visibility = if (item.isPinned==1) View.GONE else View.VISIBLE


        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.todoId == newItem.todoId
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }
    }


    interface OnItemClickListener {
        fun onItemClick(item: Todo)
        fun ondeleteButtonClick(item: Todo, position: Int)
        fun ontopButtonClick(item: Todo, position: Int)
    }

    companion object {
        private const val VIEW_TYPE_PINNED = 1
        private const val VIEW_TYPE_DEFAULT = 0
        private const val VIEW_TYPE_MANAGE = 2
        private const val VIEW_TYPE_DELAY = 3
    }
}
