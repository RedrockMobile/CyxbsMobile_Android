package com.cyxbsmobile_single.module_todo.model.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author: RayleighZ
 * Time: 2021-08-29 0:13
 */
data class TodoListSyncTimeWrapper(
    @SerializedName("sync_time")
    val syncTime: Long,
    @SerializedName("changed_todo_array")
    val todoArray: List<Todo>
): Serializable