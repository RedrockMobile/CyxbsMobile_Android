package com.cyxbsmobile_single.module_todo.viewmodel

import com.cyxbsmobile_single.module_todo.model.TodoModel
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel

/**
 * Author: RayleighZ
 * Time: 2021-08-24 6:47
 */
class TodoDetailViewModel: BaseViewModel() {

    var rawTodo: Todo? = null
    var isChanged = false

    fun updateTodo(todo: Todo, onSuccess: () -> Unit){
        TodoModel.INSTANCE.updateTodo(todo, onSuccess)
    }

    fun judgeChange(todoAfterChange: Todo): Boolean{
        isChanged = todoAfterChange != rawTodo
        return isChanged
    }

    fun delTodo(todo: Todo, onSuccess: () -> Unit){
        TodoModel.INSTANCE.delTodo(todo.todoId, onSuccess)
    }

    override fun onCleared() {
        super.onCleared()
        TodoModel.INSTANCE.rxjavaDisposables.forEach {
            if (!it.isDisposed){
                it.dispose()
            }
        }
        TodoModel.INSTANCE.rxjavaDisposables.clear()
    }
}