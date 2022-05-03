package com.redrock.module_notification.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author by OkAndGreat
 * Date on 2022/5/3 9:17.
 * 改变消息已读状态put到服务器的bean类
 */
data class ChangeReadStatusToBean(
    @SerializedName("ids")
    val ids: List<String>
) : Serializable