package com.cyxbsmobile_single.module_todo.model.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author: RayleighZ
 * Time: 2021-08-29 0:19
 */
data class TodoListPushWrapper(
    @SerializedName("data")
    val todoList: List<Todo>,
    @SerializedName("sync_time")
    val syncTime: Long,
    @SerializedName("force")
    var force: Int = 0,
    @SerializedName("first_push")
    var firsPush: Int = 1,
    @SerializedName("del_todo_array")
    var delTodoArray: List<Long> = emptyList()
): Serializable{
    companion object{
        const val IS_FORCE = 1
        const val NONE_FORCE = 0
    }
}
