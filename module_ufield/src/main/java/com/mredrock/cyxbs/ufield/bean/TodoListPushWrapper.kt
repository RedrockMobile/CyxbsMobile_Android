package com.mredrock.cyxbs.ufield.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * author : QTwawa
 * date : 2024/8/26 00:15
 */
data class TodoListPushWrapper(
    @SerializedName("data")
    val todoList: List<Todo>,
    @SerializedName("sync_time")
    val syncTime: Long,
    @SerializedName("force")
    var force: Int = 0,
    @SerializedName("first_push")
    var firsPush: Int = 1
): Serializable {
    companion object{
        const val IS_FORCE = 1
        const val NONE_FORCE = 0
    }
}
