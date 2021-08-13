package com.cyxbsmobile_single.module_todo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.`interface`.ItemChangeInterface
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper

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
    }

    private var checkedTopMark = 0
    private var isShowItem = true

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
            else -> {
                R.layout.todo_rv_item_title
            }
        }
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        ) {}
    }

    lateinit var onBindView: (itemView: View, position: Int, viewType: Int) -> Unit

    fun doOnBindView(onBindView: (itemView: View, position: Int, viewType: Int) -> Unit) {
        this.onBindView = onBindView
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
        onBindView(holder.itemView, position, curWrapper.viewType)
    }

    override fun getItemCount(): Int {
        for ((index, wrapper) in wrapperCopyList.withIndex()) {
            if (index != 0 && wrapper.viewType == TODO && wrapperCopyList[index - 1].viewType == TITLE) {
                checkedTopMark = index
            }
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

    fun delItem(position: Int) {
        wrapperCopyList.removeAt(position)
        todoItemWrapperArrayList.removeAt(position)
    }
}