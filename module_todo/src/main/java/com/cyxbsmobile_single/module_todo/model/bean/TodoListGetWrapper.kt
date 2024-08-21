package com.cyxbsmobile_single.module_todo.model.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author: RayleighZ
 * Time: 2021-08-29 1:12
 */
data class TodoListGetWrapper(
    @SerializedName("changed_todo_array")
    val todoList: List<Todo>?,
    @SerializedName("sync_time")
    val syncTime: Long,
    @SerializedName("del_todo_array")
    var delTodoArray: List<Long> = emptyList()
): Serializable
