package com.redrock.module_notification.bean

import com.google.gson.annotations.SerializedName


data class UfieldMsgBean(
    @SerializedName("activity_info")
    val activityInfo: ActivityInfo,
    @SerializedName("activity_want_to_watch_timestamp")
    val activityWantToWatchTimestamp: Long, // 1692362925
    @SerializedName("clicked")
    val clicked: Boolean, // false
    @SerializedName("examine_timestamp")
    val examineTimestamp: Long, // 1692362863
    @SerializedName("message_id")
    val messageId: Int, // 1
    @SerializedName("message_type")
    val messageType: String, // examine_report_reject
    @SerializedName("reject_reason")
    val rejectReason: String // 你太捞了
) {
    data class ActivityInfo(
        @SerializedName("activity_content")
        val activityContent: String, // ww我xwwww我xwwww我xwwww我xwwww我xwwww我xwwww我xwwww我xwwww我xwwww我xwwww我xwwww我xwwww我xwwww我xwwww我xwwww我xwwww我
        @SerializedName("activity_id")
        val activityId: Int, // 1
        @SerializedName("activity_place")
        val activityPlace: String, // 你好
        @SerializedName("activity_title")
        val activityTitle: String, // 你好
        @SerializedName("activity_type")
        val activityType: String, // culture
        @SerializedName("created_at")
        val createdAt: Long, // 1692362843
        @SerializedName("end_at")
        val endAt: Long, // 2692185999
        @SerializedName("organizer")
        val organizer: String, // 团委委委委委
        @SerializedName("registeration_type")
        val registerationType: String, // ticket
        @SerializedName("start_at")
        val startAt: Long // 1692331355
    )
}
