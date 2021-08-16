package com.cyxbsmobile_single.module_todo.viewmodel

import com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper
import com.cyxbsmobile_single.module_todo.model.database.TodoDatabase
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel

/**
 * Author: RayleighZ
 * Time: 2021-08-02 10:16
 */
class TodoViewModel : BaseViewModel() {

    //待办事项的list
    val uncheckTodoList by lazy { ArrayList<Todo>() }

    //已完成事项的list
    val checkedTodoList by lazy { ArrayList<Todo>() }

    //用于展示的带title的wrapper
    val wrapperList by lazy { ArrayList<TodoItemWrapper>() }

    //从数据库加载数据
    fun initDataList(onLoadSuccess: ()->Unit) {

        TodoDatabase.INSTANCE.todoDao()
            .queryAllTodo()
            .toObservable()
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    LogUtils.d("RayJoe", "error: $it")
                },
                onNext = {
                    LogUtils.d("RayJoe", "list size = ${it.size}")
                    it?.let {
                        for (todo in it) {
                            if (todo.isChecked) checkedTodoList.add(todo) else uncheckTodoList.add(todo)
                        }
                        wrapperList.add(TodoItemWrapper.titleWrapper("待办"))
                        for (todo in uncheckTodoList) {
                            wrapperList.add(TodoItemWrapper.todoWrapper(todo))
                        }
                        if (uncheckTodoList.isNullOrEmpty()) {
                            wrapperList.add(TodoItemWrapper(DoubleListFoldRvAdapter.EMPTY))
                        }
                        wrapperList.add(TodoItemWrapper.titleWrapper("已完成"))
                        for (todo in checkedTodoList) {
                            wrapperList.add(TodoItemWrapper.todoWrapper(todo))
                        }
                        if (checkedTodoList.isNullOrEmpty()) {
                            wrapperList.add(TodoItemWrapper(DoubleListFoldRvAdapter.EMPTY))
                        }
                    }
                    onLoadSuccess.invoke()
                }
            )
    }

    fun insertTodo(todo: Todo) {
        TodoDatabase.INSTANCE.todoDao()
            .insertTodo(todo)
            .toObservable()
            .setSchedulers()
            .safeSubscribeBy {

            }
    }

    fun insertTodoList(todoList: List<Todo>) {
        TodoDatabase.INSTANCE.todoDao()
            .insertTodoList(todoList)
    }
}