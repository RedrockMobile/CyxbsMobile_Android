package com.cyxbsmobile_single.module_todo.viewmodel

import com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper
import com.cyxbsmobile_single.module_todo.model.database.TodoDatabase
import com.mredrock.cyxbs.common.utils.ExecuteOnceObserver
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
}