package com.cyxbsmobile_single.module_todo.viewmodel

import com.cyxbsmobile_single.module_todo.model.bean.RepeatBean
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper
import com.cyxbsmobile_single.module_todo.model.database.TodoDatabase
import com.cyxbsmobile_single.module_todo.util.needTodayDone
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.ExecuteOnceObserver
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import kotlin.collections.ArrayList

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

    //需要重复提醒的todo的索引列表
    val repeatList by lazy { ArrayList<RepeatBean>() }

    //从数据库加载数据
    fun initDataList(onLoadSuccess: () -> Unit) {
        TodoDatabase.INSTANCE.todoDao()
            .queryAllTodo()
            .toObservable()
            .setSchedulers()
            .subscribe(
                ExecuteOnceObserver(
                    onExecuteOnceNext = {
                        wrapperList.clear()
                        checkedTodoList.clear()
                        uncheckTodoList.clear()
                        todoList.clear()
                        if (!it.isNullOrEmpty()) {
                            for (todo in it) {
                                //下面的逻辑是：如果到达了todo重复提醒的下一次的那一天，则将todo设定为尚未完成
                                if (todo.remindMode.repeatMode != RemindMode.NONE) {
                                    if (needTodayDone(todo.remindMode)) {
                                        todo.isChecked = false
                                    }
                                }
                                if (todo.isChecked) checkedTodoList.add(
                                    TodoItemWrapper.todoWrapper(
                                        todo
                                    )
                                )
                                else uncheckTodoList.add(TodoItemWrapper.todoWrapper(todo))
                            }
                            todoList.addAll(it)
                            wrapperList.add(TodoItemWrapper.titleWrapper("待办"))
                            for (todoWrapper in uncheckTodoList) {
                                wrapperList.add(todoWrapper)
                            }
                            wrapperList.add(TodoItemWrapper.titleWrapper("已完成"))
                            for (todoWrapper in checkedTodoList) {
                                wrapperList.add(todoWrapper)
                            }
                        } else {
                            wrapperList.add(TodoItemWrapper.titleWrapper("待办"))
                            wrapperList.add(TodoItemWrapper.titleWrapper("已完成"))
                        }
                        onLoadSuccess.invoke()
                    }
                )
            )
    }

    fun addTodo(todo: Todo, onSuccess: () -> Unit) {
        TodoDatabase.INSTANCE
            .todoDao().insertTodo(todo)
            .toObservable()
            .setSchedulers()
            .safeSubscribeBy {
                todo.todoId = it
                uncheckTodoList.add(TodoItemWrapper.todoWrapper(todo))
                wrapperList.add(TodoItemWrapper.todoWrapper(todo))
                onSuccess.invoke()
            }
    }
}