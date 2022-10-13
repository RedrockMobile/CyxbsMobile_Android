package com.redrock.module_notification.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author by OkAndGreat
 * Date on 2022/5/1 10:58.
 *
 */
data class MsgBeanData(
    @SerializedName("active_msg")
    val active_msg: List<ActiveMsgBean>,
    @SerializedName("system_msg")
    val system_msg: List<SystemMsgBean>
): Serializable



