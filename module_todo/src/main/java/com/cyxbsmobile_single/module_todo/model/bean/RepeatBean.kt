package com.cyxbsmobile_single.module_todo.model.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @date 2021-08-19
 * @author Sca RayleighZ
 * 需要重复提醒的todo的索引
 */
data class RepeatBean(
    @SerializedName("next_date")
    var nextDate: DateBeen,//下一次需要提醒的日期
    // 达到此日期的时候，将会从数据库中根据todo_id提取出对应todo，并放置到待办事项之中
    @SerializedName("todo_id")
    var todoId: Long
): Serializable
