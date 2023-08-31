package com.redrock.module_notification.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/2
 * @Description:
 *
 */
data class ItineraryAllMsg(
    var sentItineraryList: List<SentItineraryMsg>,
    var receivedItineraryList: List<ReceivedItineraryMsg>
)

/**
 * 用户已发送的行程消息
 *
 * @property id             行程消息的id
 * @property title          行程消息的标题
 * @property content        行程消息的内容
 * @property type           返回"sent"，行程消息的类型
 * @property hasStart       行程是否已开始
 * @property hasCancel      行程是否已取消提醒
 * @property updateTime     该行程通知的上次更新时间
 * @property publishTime    该消息通知的产生时间
 * @property peopleCount    该消息通知到的人数
 */
data class SentItineraryMsg(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("typ")
    val type: String,
    @SerializedName("hasStart")
    var hasStart: Boolean,
    @SerializedName("hasCancel")
    var hasCancel: Boolean,
    @SerializedName("updateTime")
    val updateTime: Long,
    @SerializedName("publishTime")
    val publishTime: Long,
    @SerializedName("peopleCount")
    val peopleCount: Int
) : Serializable

/**
 * 通知到用户的行程消息
 *
 * @property id             行程消息的id
 * @property title          行程消息的标题
 * @property content        行程消息的内容
 * @property type           返回"received"，行程消息的类型
 * @property hasStart       行程是否已开始
 * @property hasCancel      行程是否已取消提醒
 * @property updateTime     该行程通知的上次更新时间
 * @property publishTime    该消息通知的产生时间
 * @property dateJson       该行程消息的日期信息，可用于本地的事务接口
 */
data class ReceivedItineraryMsg(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("typ")
    val type: String,
    @SerializedName("hasStart")
    val hasStart: Boolean,
    @SerializedName("hasCancel")
    var hasCancel: Boolean,
    @SerializedName("updateTime")
    val updateTime: Long,
    @SerializedName("publishTime")
    val publishTime: Long,
    @SerializedName("dateJson")
    val dateJson: String,
    var hasAdded: Boolean = false
) : Serializable