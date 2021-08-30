package com.cyxbsmobile_single.module_todo.model.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
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
    var lastModifyTime: Long
) : Serializable, Comparable<Todo>{
    companion object{
        fun generateEmptyTodo(): Todo{
            return Todo(
                0,
                "",
                "",
                0,
                RemindMode.generateDefaultRemindMode(),
                System.currentTimeMillis()
            )
        }
    }

    override fun compareTo(other: Todo): Int {
        return (this.lastModifyTime - other.lastModifyTime).toInt()
    }
}