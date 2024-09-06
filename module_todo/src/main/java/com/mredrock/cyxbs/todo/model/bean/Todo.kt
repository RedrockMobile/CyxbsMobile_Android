package com.mredrock.cyxbs.todo.model.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author: RayleighZ
 * Time: 2021-08-01 17:04
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
    @SerializedName("end_time")
    var endTime: String?,//todo的截止时间
    @SerializedName("is_over")
    var isOver: Int,//是否已经过期
    @SerializedName("is_pinned")
    var isPinned: Int//是否置顶
) : Serializable{
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
                "",
                0,
                0
            )
        }
    }
}