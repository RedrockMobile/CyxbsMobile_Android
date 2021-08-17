package com.cyxbsmobile_single.module_todo.viewmodel

import android.util.Log
import com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper
import com.cyxbsmobile_single.module_todo.model.database.TodoDatabase
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.WIDGET_TODO_RAW
import com.mredrock.cyxbs.common.utils.ExecuteOnceObserver
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel

/**
 * Author: RayleighZ
 * Time: 2021-08-02 10:16
 */
class TodoViewModel : BaseViewModel() {

    //待办事项的list
    val uncheckTodoList by lazy { ArrayList<TodoItemWrapper>() }

    //已完成事项的list
    val checkedTodoList by lazy { ArrayList<TodoItemWrapper>() }

    //用于展示的带title的wrapper
    val wrapperList by lazy { ArrayList<TodoItemWrapper>() }

    //原生的所有todo
    val todoList by lazy { ArrayList<Todo>() }

    //从数据库加载数据
    fun initDataList(onLoadSuccess: () -> Unit) {
        TodoDatabase.INSTANCE.todoDao()
            .queryAllTodo()
            .toObservable()
            .setSchedulers()
            .subscribe(
                ExecuteOnceObserver(
                    onExecuteOnceNext = {
                        if (!it.isNullOrEmpty()) {
                            wrapperList.clear()
                            checkedTodoList.clear()
                            uncheckTodoList.clear()
                            todoList.clear()
                            todoList.addAll(it)
                            for (todo in it) {
                                if (todo.isChecked) checkedTodoList.add(TodoItemWrapper.todoWrapper(todo))
                                else uncheckTodoList.add(TodoItemWrapper.todoWrapper(todo))
                            }
                            wrapperList.add(TodoItemWrapper.titleWrapper("待办"))
                            for (todoWrapper in uncheckTodoList) {
                                wrapperList.add(todoWrapper)
                            }
                            if (uncheckTodoList.isNullOrEmpty()) {
                                wrapperList.add(TodoItemWrapper(DoubleListFoldRvAdapter.EMPTY))
                            }
                            wrapperList.add(TodoItemWrapper.titleWrapper("已完成"))
                            for (todoWrapper in checkedTodoList) {
                                wrapperList.add(todoWrapper)
                            }
                            if (checkedTodoList.isNullOrEmpty()) {
                                wrapperList.add(TodoItemWrapper(DoubleListFoldRvAdapter.EMPTY))
                            }
                        }
                        onLoadSuccess.invoke()
                    }
                )
            )
    }

    //为了todo小组件，将待办事项存入JSON
    fun saveToJson(){
        val uncheckedList = todoList.filter {
            !it.isChecked
        }
        BaseApp.context.defaultSharedPreferences.editor {
            LogUtils.d("MasterRay", Gson().toJson(uncheckedList))
            putString(WIDGET_TODO_RAW, Gson().toJson(uncheckedList))
            apply()
        }
    }
}