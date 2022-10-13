package com.redrock.module_notification.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author by OkAndGreat
 * Date on 2022/5/3 9:20.
 * 删除消息返回值的Bean类
 */
data class DeleteMsgFromBean(
    @SerializedName("info")
    val info: String,
    @SerializedName("status")
    val status: Int
) : Serializable