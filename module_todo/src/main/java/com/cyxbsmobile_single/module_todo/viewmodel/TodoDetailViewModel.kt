package com.cyxbsmobile_single.module_todo.viewmodel

import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.database.TodoDatabase
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import io.reactivex.Observable

/**
 * Author: RayleighZ
 * Time: 2021-08-24 6:47
 */
class TodoDetailViewModel: BaseViewModel() {

    var rawTodo: Todo? = null
    var isChanged = false

    fun updateTodo(todo: Todo, onSuccess: () -> Unit){
        Observable.just(todo)
            .map {
                TodoDatabase.INSTANCE.todoDao().updateTodo(todo)
            }
            .setSchedulers()
            .safeSubscribeBy {
                onSuccess.invoke()
            }

    }

    fun judgeChange(todoAfterChange: Todo): Boolean{
        isChanged = todoAfterChange != rawTodo
        return isChanged
    }
}