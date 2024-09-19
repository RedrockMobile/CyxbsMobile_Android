package com.mredrock.cyxbs.ufield.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * author : QTwawa
 * date : 2024/8/26 00:14
 */
@Entity(tableName = "todo_list")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("todo_id")
    var todoId: Long,//用户todo的唯一标识
    @SerializedName("title")
    var title: String,//todo的标题
    @SerializedName("detail")
    var detail: String,//todo的详情
    @SerializedName("is_done")
    var isChecked: Int,//todo是否已经完成
    @SerializedName("remind_mode")
    var remindMode: RemindMode,
    @SerializedName("last_modify_time")
    var lastModifyTime: Long,
    @SerializedName("type")
    var type: String,
    @SerializedName("is_pinned")
    var isPinned: Int,//是否置顶
    @SerializedName("end_time")
    var endTime: String
) : Serializable {
    companion object {
        fun generateEmptyTodo(): Todo {
            return Todo(
                0,
                "",
                "",
                0,
                RemindMode.generateDefaultRemindMode(),
                System.currentTimeMillis(),
                "other",
                0,
                ""
            )
        }
    }
}
