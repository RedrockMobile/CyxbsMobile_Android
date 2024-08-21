package com.cyxbsmobile_single.module_todo.model.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * description:
 * author: sanhuzhen
 * date: 2024/8/21 19:17
 */
data class TodoPinData(
    @SerializedName("force")
    val force: Int,
    @SerializedName("pin")
    val pin: Int,
    @SerializedName("sync_time")
    val syncTime: Int,
    @SerializedName("todo_id")
    val todoId: Int
): Serializable{
    companion object{
        const val IS_FORCE = 1
        const val NONE_FORCE = 0

        const val IS_PIN = 1
        const val NONE_PIN = 0
    }
}