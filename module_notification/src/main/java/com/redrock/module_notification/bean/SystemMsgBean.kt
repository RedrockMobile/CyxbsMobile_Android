package com.redrock.module_notification.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author by OkAndGreat
 * Date on 2022/5/1 11:01.
 *
 */
data class SystemMsgBean(
    @SerializedName("content")
    val content: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("has_read")
    var has_read: Boolean,
    @SerializedName("id")
    val id: Int,
    @SerializedName("md")
    val md: String,
    @SerializedName("pic_url")
    val pic_url: String,
    @SerializedName("publish_time")
    val publish_time: Long,
    @SerializedName("redirect_url")
    val redirect_url: String,
    @SerializedName("stu_num")
    val stu_num: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("typ")
    val typ: Int,
    @SerializedName("user_head_url")
    val user_head_url: String,
    @SerializedName("user_name")
    val user_name: String
) : Serializable