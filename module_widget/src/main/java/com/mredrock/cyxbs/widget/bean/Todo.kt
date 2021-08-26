package com.mredrock.cyxbs.widget.bean

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @date 2021-08-17
 * @author Sca RayleighZ
 */
data class Todo(
    @SerializedName("todo_id")
    var todoId: Long,//用户todo的唯一标识
    @SerializedName("title")
    var title: String,//todo的标题
    @SerializedName("detail")
    var detail: String,//todo的详情
    @SerializedName("is_done")
    var isChecked: Boolean,//todo是否已经完成
    @SerializedName("remind_mode")
    var remindMode: RemindMode,
    @SerializedName("last_modify_time")
    var lastModifyTime: Long
) : Serializable{
    companion object{
        fun generateEmptyTodo(): Todo{
            return Todo(
                0,
                "",
                "",
                false,
                RemindMode.generateDefaultRemindMode(),
                System.currentTimeMillis()
            )
        }
    }
}