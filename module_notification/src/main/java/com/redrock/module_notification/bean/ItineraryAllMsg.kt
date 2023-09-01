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
    var sentItineraryList: List<SentItineraryMsgBean>,
    var receivedItineraryList: List<ReceivedItineraryMsgBean>
)

/**
 * 用户已发送的行程消息
 *
 * @property id             行程消息的id
 * @property title          行程消息的标题
 * @property content        行程消息的内容
 * @property type           返回"sent"，行程消息的类型
 * @property hasRead        该条行程是否已读的状态位
 * @property hasStart       行程是否已开始
 * @property hasCancel      行程是否已取消提醒
 * @property updateTime     该行程通知的上次更新时间
 * @property publishTime    该消息通知的产生时间
 * @property peopleCount    该消息通知到的人数
 */
data class SentItineraryMsgBean(
    @SerializedName("id")
    val id: Int,                // 每条消息的id都是独一无二的
    @SerializedName("title")
    val title: String,          // 一般是“学习”、“开会”之类的
    @SerializedName("content")
    val content: String,        // 行程的具体内容，格式为“你提醒XXX干啥”
    @SerializedName("typ")
    val type: String,           // 该条行程的类型，有sent和received之分
    @SerializedName("hasRead")
    var hasRead: Boolean,       // 该条行程是否已读
    @SerializedName("hasStart")
    var hasStart: Boolean,      // 该条行程是否已开始
    @SerializedName("hasCancel")
    var hasCancel: Boolean,     // 该条行程是否被取消提醒
    @SerializedName("updateTime")
    val updateTime: Long,       // 该条行程上次更新时的时间戳
    @SerializedName("publishTime")
    val publishTime: Long,      // 该条行程产生时的时间戳
    @SerializedName("peopleCount")
    val peopleCount: Int        // 该条行程通知到的人数
) : Serializable

/**
 * 通知到用户的行程消息
 *
 * @property id             行程消息的id
 * @property title          行程消息的标题
 * @property content        行程消息的内容
 * @property type           返回"received"，行程消息的类型
 * @property hasAdd         该条行程是否已被加入日程（课表事务）
 * @property hasRead        该条行程是否已读的状态位
 * @property hasStart       行程是否已开始
 * @property hasCancel      行程是否已取消提醒
 * @property updateTime     该行程通知的上次更新时间
 * @property publishTime    该消息通知的产生时间
 * @property dateJson       该行程消息的日期信息，可用于本地的事务接口
 */
data class ReceivedItineraryMsgBean(
    @SerializedName("id")
    val id: Int,                // 每条消息的id都是独一无二的
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,        // 行程的具体内容，格式为“xxx提醒你…………”
    @SerializedName("typ")
    val type: String,           // 该条行程的类型，有sent和received之分
    @SerializedName("hasAdd")
    var hasAdd: Boolean,        // 该条行程是否已被加入日程（课表事务）
    @SerializedName("hasRead")
    var hasRead: Boolean,       // 该条行程是否已读
    @SerializedName("hasStart")
    var hasStart: Boolean,      // 该条行程是否已开始
    @SerializedName("hasCancel")
    var hasCancel: Boolean,     // 该条行程是否被取消提醒
    @SerializedName("updateTime")
    val updateTime: Long,       // 该条行程上次更新时的时间戳
    @SerializedName("publishTime")
    val publishTime: Long,      // 该条行程产生时的时间戳
    @SerializedName("dateJson")
    val dateJson: ItineraryDateBean,

    ) : Serializable {
    data class ItineraryDateBean(
        @SerializedName("beginLesson")
        val beginLesson: Int,   // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
        @SerializedName("day")
        val day: Int,           // 星期数，这里的星期一为 1，注意与添加事务的day不一样，那里的day是星期一为 0
        @SerializedName("period")
        val period: Int,        // 长度，几节课
        @SerializedName("week")
        val week: Int           // 第几周，整学期为0
    ) : Serializable
}

data class ChangeItineraryReadStatusUploadBean(
    @SerializedName("id")
    val ids: List<Int>,
    @SerializedName("status")
    var status: Boolean = true
) : Serializable

/**
 * 转 Json的dataBean，转为Json后作为添加事务的dateJson参数使用
 *
 * @property beginLesson
 * @property day
 * @property period
 * @property week
 */
data class AffairDateBean(
    @SerializedName("begin_lesson")
    val beginLesson: Int,
    @SerializedName("day")
    val day: Int,
    @SerializedName("period")
    val period: Int,
    @SerializedName("week")
    val week: List<Int>
) : Serializable

fun ReceivedItineraryMsgBean.ItineraryDateBean.toAffairDateBean(): AffairDateBean {
    return AffairDateBean(this.beginLesson, this.day, this.period, listOf(this.week))
}