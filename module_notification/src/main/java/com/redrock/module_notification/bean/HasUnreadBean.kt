package com.redrock.module_notification.bean

import com.google.gson.annotations.SerializedName

/**
 * Author by OkAndGreat
 * Date on 2022/5/1 10:50.
 *
 */
data class UnreadData(
    @SerializedName("has")
    val has: Boolean
)