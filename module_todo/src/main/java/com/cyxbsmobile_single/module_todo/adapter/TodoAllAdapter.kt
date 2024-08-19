import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Paint
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R
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
        isEnabled = enabled
        // 当退出 manage 状态时，重置所有复选框的选中状态
        if (!isEnabled) {
            itemSelectionState.clear() // 清除所有选中状态
        }
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

    fun deleteSelectedItems() {
        val currentList = currentList.toMutableList()
        for (i in itemCount - 1 downTo 0) {
            if (itemSelectionState[i, false]) {
                currentList.removeAt(i)
                itemSelectionState.delete(i)
            }
        }
        submitList(currentList)

    }
    fun topSelectedItems() {
        val currentList = currentList.toMutableList()

        // 筛选出选中的项，并按它们在列表中的顺序排序
        val selectedItems = currentList.filterIndexed { index, item ->
            val isSelected = itemSelectionState.get(index, false)
            if (isSelected) {
                // 将选中的项加入到 pinnedItems 中
                pinnedItems.add(item)
            }
            isSelected
        }

        // 将选中的项从原来的位置移除
        currentList.removeAll(selectedItems)

        // 将选中的项按原顺序添加到顶部
        currentList.addAll(0, selectedItems)

        // 提交更新后的列表
        submitList(currentList) {
            // 滚动到顶部以显示置顶的项
            notifyDataSetChanged()
        }

        // 清空选中状态
        itemSelectionState.clear()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val isSelected = itemSelectionState.get(position, false)
        holder.checkbox.isChecked = isSelected
        holder.bind(item)
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            itemSelectionState.put(position, isChecked)
        }
        if (!isEnabled) {
            holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
                itemSelectionState.put(position, isChecked)
                if (isChecked) {
                    animateCheckBox(holder.checkbox)
                    animateStrikeThrough(holder.listtext, true)
                    holder.listtext.setTextColor(holder.listtext.context.getColor(R.color.todo_check_item_color))
                } else {
                    animateStrikeThrough(holder.listtext, false)
                    holder.listtext.setTextColor(holder.listtext.context.getColor(R.color.todo_title))
                }
            }
        } else {
            holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
                itemSelectionState.put(position, isChecked)
            }
        }
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
    public override fun getItem(position: Int): Todo {
        return super.getItem(position)
    }
    inner class ViewHolder(itemView: View, private val listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val listtext: TextView = itemView.findViewById(R.id.todo_title_text)
        private val date: TextView = itemView.findViewById(R.id.todo_notify_time)
        private val deletebutton: LinearLayout = itemView.findViewById(R.id.todo_delete)
        private val topbutton: LinearLayout = itemView.findViewById(R.id.todo_item_totop)
        val checkbox:CheckBox=itemView.findViewById(R.id.todo_item_check)

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
    private fun animateCheckBox(checkBox: CheckBox) {
        // 创建CheckBox的动画，使其缓慢打上勾
        ObjectAnimator.ofFloat(checkBox, "alpha", 0f, 1f).apply {
            duration = 500 // 动画时长
            start()
        }
    }

    private fun animateStrikeThrough(textView: TextView, isStrikingThrough: Boolean) {
        val start = if (isStrikingThrough) 0 else textView.width
        val end = if (isStrikingThrough) textView.width else 0

        ValueAnimator.ofInt(start, end).apply {
            duration = 500 // 动画时长
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                textView.paintFlags = if (isStrikingThrough) {
                    textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                textView.invalidate()
            }
            start()
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
    }
}
