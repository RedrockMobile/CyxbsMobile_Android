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
import java.util.Collections

/**
 * @Project: CyxbsMobile_Android
 * @File: TodoAllAdapter
 * @Author: 86199
 * @Date: 2024/8/12
 * @Description: todo模块rv的adapter
 */

class TodoAllAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Todo, TodoAllAdapter.ViewHolder>(ItemDiffCallback()), ItemTouchHelperAdapter {

    var pinnedItems: MutableList<Todo> = mutableListOf()
    var isEnabled = false

    //SparseBooleanArray适合处理较大的数据集合。
    var itemSelectionState = SparseBooleanArray()

    private val selectedItems = mutableListOf<Todo>()

    // 用于保存选中项的 ID 集合
    private val selectedIds = mutableSetOf<Int>()


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
            itemSelectionState.clear()
            notifyItemRangeChanged(0, itemCount)
        }
        Log.d("TodoAllAdapter", "setEnabled called with: $isEnabled")
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = when (viewType) {
            VIEW_TYPE_MANAGE -> R.layout.todo_rv_item_manage
            VIEW_TYPE_PINNED -> R.layout.todo_rv_item_todo_pinned
            VIEW_TYPE_DEFAULT -> R.layout.todo_rv_item_todo
            else -> throw IllegalArgumentException("Unknown view type")
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view, listener,viewType)
    }

    fun deleteSelectedItems() {
        val currentList = currentList.toMutableList()
        for (i in itemCount - 1 downTo 0) {
            if (itemSelectionState.get(i, false)) {
                currentList.removeAt(i)
                itemSelectionState.delete(i)
            }
        }
        submitList(currentList)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun selectedall(){
        // 清空当前的状态
        itemSelectionState.clear()
        // 遍历所有项，将它们的索引和默认状态添加到 SparseBooleanArray 中
        for (i in 0 until itemCount) {
            itemSelectionState.put(i, true) //勾选全部
        }
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun toSelectedall(){
        // 清空当前的状态
        itemSelectionState.clear()
        // 遍历所有项，将它们的索引和默认状态添加到 SparseBooleanArray 中
        for (i in 0 until itemCount) {
            itemSelectionState.put(i, false) //取消勾选全部
        }
        notifyDataSetChanged()
    }
    fun topSelectedItems() {
        val currentList = currentList.toMutableList()
        val selectedItems = currentList.filterIndexed { index, _ ->
            itemSelectionState.get(index, false)
        }

        // 将选中的项添加到 pinnedItems 列表中
        pinnedItems.addAll(selectedItems)

        // 从 currentList 中移除选中的项
        currentList.removeAll(selectedItems)

        // 将选中的项添加到 currentList 的顶部
        currentList.addAll(0, selectedItems)

        // 提交更新后的列表并刷新 RecyclerView
        submitList(currentList)
        // 清空选中状态
        itemSelectionState.clear()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val isSelected = itemSelectionState.get(position, false)
        Log.d("TodoAllAdapter", "onBindViewHolder - Position: $position, isEnabled: $isEnabled, isSelected: $isSelected")
        if (isEnabled) {
            holder.checkbox?.isChecked = isSelected
            holder.checkbox?.setOnCheckedChangeListener { _, isChecked ->
                itemSelectionState.put(position, isChecked)
            }

        } else {
           holder.defaultcheckbox

        }
        holder.bind(item)
    }


    override fun getItemViewType(position: Int): Int {
        // 如果启用了管理模式，所有项应使用管理模式布局
        val item = getItem(position)
        return if (pinnedItems.contains(item)) {
            VIEW_TYPE_PINNED
        } else {
            if (isEnabled) {
              VIEW_TYPE_MANAGE
            } else {
                VIEW_TYPE_DEFAULT
            }
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
            topbutton.visibility = if (pinnedItems.contains(item)) View.GONE else View.VISIBLE


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
    }
}
