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
    @SerializedName("remind_time")
    var remindTime: Long,//todo的提醒时间
    @SerializedName("repeat_mode")
    var repeatMode: Int,//todo的重复模式
    // 0:不重复、1:每日重复、2:每周重复、3:每月重复、4:每年重复
    @SerializedName("detail")
    var detail: String,//todo的详情
    @SerializedName("is_checked")
    var isChecked: Boolean,//todo是否已经完成
    @SerializedName("last_modify_time")
    var lastModifyTime: Long//这条todo的最后修改日期
) : Serializable