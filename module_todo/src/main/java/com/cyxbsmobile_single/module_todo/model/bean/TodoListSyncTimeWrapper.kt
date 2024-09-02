package com.cyxbsmobile_single.module_todo.model.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author: RayleighZ
 * Time: 2021-08-29 0:13
 */
data class TodoListSyncTimeWrapper(
    @SerializedName("changed_todo_array")
    var todoArray: List<Todo>,  @SerializedName("sync_time")
    val syncTime: Long
): Serializable