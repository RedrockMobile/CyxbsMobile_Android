package com.cyxbsmobile_single.module_todo.adapter
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
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
    var selectItems: MutableList<Todo> = mutableListOf()


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
            VIEW_TYPE_DELAY -> R.layout.todo_rv_item_delay
            VIEW_TYPE_DEFAULT_NO -> R.layout.todo_rv_item_todo_notime
            VIEW_TYPE_PINNED_NO -> R.layout.todo_rv_item_todo_pinned_notime
            VIEW_TYPE_MANAGE_NO ->R.layout.todo_rv_item_manage_notime
            VIEW_TYPE_MANAGE_PINNED ->R.layout.todo_rv_item_manage_pinned
            VIEW_TYPE_MANAGE_PINNED_NO ->R.layout.todo_rv_item_manage_pinned_no
            else -> throw IllegalArgumentException("Unknown view type")
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view, listener, viewType)
    }

    fun deleteSelectedItems() {
        val currentList = currentList.toMutableList()
        // 遍历当前列表，从后往前删除选中的项
        for (i in itemCount - 1 downTo 0) {
            if (selectItems.contains(currentList[i])) {
                currentList.removeAt(i)
            }
        }
        submitList(currentList)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectedall() {
        // 清空当前的选中项
        selectItems.clear()
        // 将所有项添加到 selectItems 中
        selectItems.addAll(currentList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun toSelectedall() {
        // 清空当前的状态
        // 清空当前的选中项
        selectItems.clear()
        notifyDataSetChanged()
    }

    fun topSelectedItems() {
        val currentList = currentList.toMutableList()  // 获取当前列表的可变副本

        // 过滤出要置顶的选中项，并将其从 currentList 中移除
        val selectedItems = currentList.filter { it in selectItems }.toMutableList()

        if (selectedItems.isEmpty()) return // 如果没有选中项，直接返回

        // 更新 selectedItems 的 isPinned 状态
        selectedItems.forEach { it.isPinned = 1 }

        // 从 currentList 中移除所有选中的项
        currentList.removeAll(selectedItems.toSet())

        // 将选中的项添加到 currentList 的顶部
        currentList.addAll(0, selectedItems)

        // 提交更新后的列表并刷新RecyclerView
        submitList(currentList) {
            // 清空选中状态，确保同步
            selectItems.clear()
            Log.d("TodoAllAdapter", "List updated, cleared selected items")
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        // 动态设置宽度，在绑定数据时设置 EditText 的宽度
        holder.listtext.post {
            // 获取当前宽度
            val originalWidth = holder.listtext.width

            // 将 dp 转换为 px
            val extraWidthInDp = 9
            val extraWidthInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                extraWidthInDp.toFloat(),
                holder.itemView.context.resources.displayMetrics
            ).toInt()

            // 设置新的宽度
            val newWidth = originalWidth + extraWidthInPx
            holder.listtext.layoutParams.width = newWidth
            holder.listtext.requestLayout() // 更新视图
        }

        val isSelected = selectItems.contains(item)
        val itemTime = if (!item.remindMode.notifyDateTime.isNullOrEmpty()) {
            try {
                dateFormat.parse(item.remindMode.notifyDateTime)?.time ?: 0L
            } catch (e: ParseException) {
                e.printStackTrace()
                System.currentTimeMillis()
            }
        } else {
            0L
        }
        val currentTime = System.currentTimeMillis()
        holder.defaultcheckbox?.setStatusWithAnime(false)
        holder.checkbox?.isChecked = false
        holder.listtext.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                R.color.todo_check_line_color
            )
        )
        if (item.isPinned == 1) {
            holder.listtext.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.todo_item_text_top_color
                )
            )
        }
       if (currentTime>itemTime&&item.endTime!=""){
           holder.listtext.setTextColor(
               ContextCompat.getColor(
                   holder.itemView.context,
                   R.color.todo_item_delay_color
               )
           )
       }

        holder.icRight?.visibility = View.GONE
        Log.d(
            "TodoAllAdapter",
            "onBindViewHolder - Position: $position, isEnabled: $isEnabled, isSelected: $isSelected"
        )
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
        val itemTime = if (!item.remindMode.notifyDateTime.isNullOrEmpty()) {
            try {
                dateFormat.parse(item.remindMode.notifyDateTime)?.time ?: 0L
            } catch (e: ParseException) {
                e.printStackTrace()
                System.currentTimeMillis()
            }
        } else {
            0L
        }
        val currentTime = System.currentTimeMillis()

        // 默认的viewType
        var type = VIEW_TYPE_DEFAULT

        if (isEnabled) {
            if (item.isPinned==1){
           type= if (item.endTime == "") VIEW_TYPE_MANAGE_PINNED else VIEW_TYPE_MANAGE_PINNED_NO
            }else {
                type = if (item.endTime == "") VIEW_TYPE_MANAGE else VIEW_TYPE_MANAGE_NO
            }
            } else {
            // 检查置顶和 endTime 为空的条件
            if (item.isPinned == 1) {
                type = if (item.endTime != "") VIEW_TYPE_PINNED else VIEW_TYPE_PINNED_NO
            } else if (currentTime > itemTime && item.endTime != "") {
                // 如果时间已过期
                type = VIEW_TYPE_DELAY
            } else if (item.endTime == "") {
                // endTime 为空的默认类型
                type = VIEW_TYPE_DEFAULT_NO
            }
        }

        return type
    }


    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val currentList = currentList.toMutableList()

        // 如果置顶的项参与交换，则不进行交换
        if (currentList[fromPosition].isPinned == 1 || currentList[toPosition].isPinned == 1) {
            return
        }
        Collections.swap(currentList, fromPosition, toPosition)
        submitList(currentList)
    }

    public override fun getItem(position: Int): Todo {
        return if (position >= 0 && position < currentList.size) {
            currentList[position]
        } else {
            currentList[0]
        }
    }

    inner class ViewHolder(
        itemView: View,
        private val listener: OnItemClickListener,
        private val viewType: Int
    ) :
        RecyclerView.ViewHolder(itemView) {

        val listtext =
            if (!isEnabled) itemView.findViewById<AppCompatEditText>(R.id.todo_title_text) else itemView.findViewById<TextView>(
                R.id.todo_title_text
            )
        private val date: TextView = itemView.findViewById(R.id.todo_notify_time)
        private val deletebutton: LinearLayout = itemView.findViewById(R.id.todo_delete)
        private val topbutton: LinearLayout = itemView.findViewById(R.id.todo_item_totop)
        val icRight: ImageView? = itemView.findViewById(R.id.todo_iv_check)
        val defaultcheckbox: CheckLineView? = if (!isEnabled) {
            itemView.findViewById(R.id.todo_item_check) as? CheckLineView
        } else {
            null
        }
        val checkbox: CheckBox? = if (isEnabled) {
            itemView.findViewById(R.id.todo_item_check) as? CheckBox
        } else {
            null
        }

        fun bind(item: Todo) {
            listtext.setText(item.title)
            date.text = item.remindMode.notifyDateTime
            if (item.isChecked == 1) {
                listtext.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.todo_check_item_trans_color
                    )
                )
                icRight?.let {
                    it.visibility = View.VISIBLE
                }
                defaultcheckbox?.setStatusWithAnime(true)
            }
            if (item.remindMode.notifyDateTime == "") {
                listener.onItemnotify(item)
            }
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
            listtext.setOnClickListener {
                listener.onItemClick(item)
            }
            if (!isEnabled) {
                defaultcheckbox?.setOnClickListener {
                    listtext.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.todo_check_item_trans_color
                        )
                    )
                    icRight?.let {
                        it.visibility = View.VISIBLE
                    }
                    defaultcheckbox.setStatusWithAnime(true)
                    listener.onFinishCheck(item)
                }
            }
            // 如果当前项是置顶项，则隐藏置顶按钮
            topbutton.visibility = if (item.isPinned == 1) View.GONE else View.VISIBLE
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
        fun onListtextClick(item: Todo)
        fun ondeleteButtonClick(item: Todo, position: Int)
        fun ontopButtonClick(item: Todo, position: Int)
        fun onFinishCheck(item: Todo)
        fun onItemnotify(item: Todo)
    }

    companion object {
        private const val VIEW_TYPE_PINNED = 1
        private const val VIEW_TYPE_PINNED_NO = 4
        private const val VIEW_TYPE_DEFAULT = 0
        private const val VIEW_TYPE_DEFAULT_NO = 5
        private const val VIEW_TYPE_MANAGE = 2
        private const val VIEW_TYPE_DELAY = 3
        private const val VIEW_TYPE_MANAGE_NO = 6
        private const val VIEW_TYPE_MANAGE_PINNED = 7
        private const val VIEW_TYPE_MANAGE_PINNED_NO = 8

    }
}