package com.cyxbsmobile_single.module_todo.viewmodel

import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.model.TodoModel
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.model.bean.RepeatBean
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper
import com.cyxbsmobile_single.module_todo.util.getString
import com.cyxbsmobile_single.module_todo.util.needTodayAddIn
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

    //需要重复提醒的todo的索引列表
    val repeatList by lazy { ArrayList<RepeatBean>() }

    //从数据库加载数据
    fun initDataList(
        onLoadSuccess: () -> Unit,
        onConflict: ((remoteSyncTime: Long, localSyncTime: Long) -> Unit)? = null
    ) {
        TodoModel.INSTANCE.getTodoList(
            onSuccess = {
                wrapperList.clear()
                checkedTodoList.clear()
                uncheckTodoList.clear()
                todoList.clear()
                if (!it.isNullOrEmpty()) {
                    for (todo in it) {
                        //下面的逻辑是：如果到达了todo重复提醒的下一次的那一天，则将todo设定为尚未完成
                        if (todo.remindMode.repeatMode != RemindMode.NONE) {
                            if (needTodayAddIn(todo)) {
                                //这里需要判断今天是不是已经添加进来一次了
                                todo.isChecked = 0
                            }
                        }
                        if (todo.getIsChecked()) checkedTodoList.add(
                            TodoItemWrapper.todoWrapper(
                                todo
                            )
                        )
                        else uncheckTodoList.add(TodoItemWrapper.todoWrapper(todo))
                    }
                    todoList.addAll(it)
                    wrapperList.add(TodoItemWrapper.titleWrapper(getString(R.string.todo_string_uncheck)))
                    wrapperList.addAll(uncheckTodoList)
                    wrapperList.add(TodoItemWrapper.titleWrapper(getString(R.string.todo_string_checked)))
                    wrapperList.addAll(checkedTodoList)
                } else {
                    wrapperList.add(TodoItemWrapper.titleWrapper(getString(R.string.todo_string_uncheck)))
                    wrapperList.add(TodoItemWrapper.titleWrapper(getString(R.string.todo_string_checked)))
                }
                onLoadSuccess.invoke()
            },
            onConflict = onConflict
        )
    }

    fun addTodo(todo: Todo, onSuccess: () -> Unit) {
        TodoModel.INSTANCE
            .addTodo(todo) {
                todo.todoId = it
                uncheckTodoList.add(0, TodoItemWrapper.todoWrapper(todo))
                wrapperList.add(0, TodoItemWrapper.todoWrapper(todo))
                onSuccess.invoke()
            }
    }
}