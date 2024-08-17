package com.cyxbsmobile_single.module_todo.model.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TodoTypeListWrapper(
    @SerializedName("changed_todo_array")
    val changedTodoArray: List<ChangedTodoArray>,
    @SerializedName("del_todo_array")
    val delTodoArray: List<Int>,
    @SerializedName("sync_time")
    val syncTime: Long
) : Serializable {
    data class ChangedTodoArray(
        @SerializedName("date")
        val date: String,
        @SerializedName("detail")
        val detail: String,
        @SerializedName("is_done")
        val isDone: Int,
        @SerializedName("last_modify_time")
        val lastModifyTime: Int,
        @SerializedName("remind_mode")
        val remindMode: RemindMode,
        @SerializedName("title")
        val title: String,
        @SerializedName("todo_id")
        val todoId: Int,
        @SerializedName("type")
        val type: String
    ) : Serializable
}





