package com.mredrock.cyxbs.affair.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * author : QT
 * date : 2024/8/23 21:20
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
