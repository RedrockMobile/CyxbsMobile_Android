package com.cyxbsmobile_single.module_todo.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper
import com.cyxbsmobile_single.module_todo.util.remindTimeStamp2String
import com.mredrock.cyxbs.common.utils.LogUtils
import kotlinx.android.synthetic.main.todo_rv_item_empty.view.*
import kotlinx.android.synthetic.main.todo_rv_item_title.view.*
import kotlinx.android.synthetic.main.todo_rv_item_todo.view.*

/**
 * Author: RayleighZ
 * Time: 2021-08-02 11:27
 * 双列表可折叠RecyclerView
 */
@Suppress("UNCHECKED_CAST")
class DoubleListFoldRvAdapter(
    private val todoItemWrapperArrayList: ArrayList<TodoItemWrapper>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object {
        const val TODO = 0
        const val TITLE = 1
        const val EMPTY = 2
    }

    private var checkedTopMark = 0
    private var isShowItem = true

    //position更新用
    private val positionChangeArray by lazy { ArrayList<Int>() }

    //真正用于展示的list，会动态的增减
    private var wrapperCopyList: ArrayList<TodoItemWrapper> =
        todoItemWrapperArrayList.clone() as ArrayList<TodoItemWrapper>

    override fun getItemViewType(position: Int): Int = todoItemWrapperArrayList[position].viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutId = when (viewType) {
            TODO -> {
                R.layout.todo_rv_item_todo
            }
            TITLE -> {
                R.layout.todo_rv_item_title
            }
            EMPTY -> {
                R.layout.todo_rv_item_empty
            }
            else -> {
                R.layout.todo_rv_item_title
            }
        }
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        ) {  }
    }

    lateinit var onBindView: (itemView: View, position: Int, viewType: Int, wrapper: TodoItemWrapper) -> Unit

    fun doOnBindView(onBindView: (itemView: View, position: Int, viewType: Int, wrapper: TodoItemWrapper) -> Unit) {
        this.onBindView = onBindView
    }

    fun checkItem(wrapper: TodoItemWrapper){
        wrapperCopyList.remove(wrapper)
        wrapper.todo?.isChecked = true
        wrapperCopyList.add(checkedTopMark, wrapper)

        val diffRes =
            DiffUtil.calculateDiff(DiffCallBack(todoItemWrapperArrayList, wrapperCopyList))
        diffRes.dispatchUpdatesTo(this)

        //同步修改todoItemWrapperArrayList
        todoItemWrapperArrayList.remove(wrapper)
        wrapper.todo?.isChecked = true
        todoItemWrapperArrayList.add(checkedTopMark, wrapper)
    }

    fun delItem(wrapper: TodoItemWrapper) {
        wrapperCopyList.remove(wrapper)
        val diffRes =
            DiffUtil.calculateDiff(DiffCallBack(todoItemWrapperArrayList, wrapperCopyList))
        diffRes.dispatchUpdatesTo(this)
        todoItemWrapperArrayList.remove(wrapper)
    }

    private fun foldItem() {
        wrapperCopyList.subList(checkedTopMark, itemCount).clear()
        val diffRes =
            DiffUtil.calculateDiff(DiffCallBack(todoItemWrapperArrayList, wrapperCopyList))
        diffRes.dispatchUpdatesTo(this)
    }

    private fun showItem() {
        val diffRes =
            DiffUtil.calculateDiff(DiffCallBack(wrapperCopyList, todoItemWrapperArrayList))
        wrapperCopyList.clear()
        wrapperCopyList.addAll(todoItemWrapperArrayList)
        diffRes.dispatchUpdatesTo(this)
    }


    fun changeFoldStatus() {
        if (isShowItem) {
            foldItem()
        } else {
            showItem()
        }
        isShowItem = !isShowItem
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curWrapper = wrapperCopyList[position]
        val itemView = holder.itemView
        //在这里进行基础的数据类绑定

        when(curWrapper.viewType){
            TITLE -> {
                itemView.todo_tv_item_title.text = curWrapper.title
            }

            TODO -> {
                itemView.apply {
                    curWrapper.todo?.let { todo ->
                        todo_fl_del.visibility = View.GONE
                        todo_tv_todo_title.text = todo.title
                        todo_tv_notify_time.text = remindTimeStamp2String(todo.remindTime)
                        todo_clv_todo_item.setStatusWithoutAnime(todo.isChecked)
                        if (todo.isChecked){
                            //TODO: 替换为res资源
                            todo_tv_todo_title.setTextColor(Color.parseColor("#6615315B"))
                            todo_iv_check.visibility = View.VISIBLE
                        } else {
                            todo_iv_check.visibility = View.GONE
                        }
                    }
                }
            }

            EMPTY -> {
                itemView.apply {
                    if (position != 1){
                        //认定为下方的缺省
                        todo_rv_item_iv_empty.setImageResource(R.drawable.todo_ic_empty2)
                        todo_rv_item_tv_empty.text = "还没有已完成事项哦，期待你的好消息！"
                    }
                }
            }
        }
        onBindView(holder.itemView, position, curWrapper.viewType, curWrapper)
    }

    override fun getItemCount(): Int {
        for ((index, wrapper) in wrapperCopyList.withIndex()) {
            if (index != 0 && wrapper.viewType == TODO && wrapperCopyList[index - 1].viewType == TITLE) {
                checkedTopMark = index
            }
            //记录原始的position
            positionChangeArray.add(index, index)
        }
        return wrapperCopyList.size
    }

    inner class DiffCallBack(
        private val oldList: ArrayList<TodoItemWrapper>,
        private val newList: ArrayList<TodoItemWrapper>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return if (oldItem.viewType == newItem.viewType) {
                if (oldItem.viewType == TITLE) {
                    oldItem.title == newItem.title
                } else {
                    oldItem.todo?.todoId == newItem.todo?.todoId
                }
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.toString() == newItem.toString()
        }

    }
}