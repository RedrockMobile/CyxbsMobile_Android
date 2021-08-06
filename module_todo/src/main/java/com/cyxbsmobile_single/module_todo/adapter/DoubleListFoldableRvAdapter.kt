package com.cyxbsmobile_single.module_todo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper

/**
 * Author: RayleighZ
 * Time: 2021-08-02 11:27
 */
class DoubleListFoldableRvAdapter(
    val todoItemWrapperArrayList: ArrayList<TodoItemWrapper>,
    private val onBindView: (itemView: View, position: Int, viewType: Int)->Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TODO = 0
        const val TITLE = 1
    }

    var checkedTopMark = 0

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
        ){ }
    }

    fun foldItem(){
        this.notifyItemRangeRemoved(checkedTopMark, itemCount)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindView(holder.itemView, position, todoItemWrapperArrayList[position].viewType)
    }

    override fun getItemCount(): Int {
        for ( (index, wrapper) in todoItemWrapperArrayList.withIndex()){
            if (wrapper.viewType == TODO || todoItemWrapperArrayList[index].viewType == TITLE){
                checkedTopMark = index
            }
        }
        return todoItemWrapperArrayList.size
    }

}