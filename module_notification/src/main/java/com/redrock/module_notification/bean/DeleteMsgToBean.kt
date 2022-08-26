package com.redrock.module_notification.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author by OkAndGreat
 * Date on 2022/5/3 9:20.
 * 删除消息传到服务器的bean类
 */
data class DeleteMsgToBean(
    @SerializedName("ids")
    val ids: List<String>
) : Serializable