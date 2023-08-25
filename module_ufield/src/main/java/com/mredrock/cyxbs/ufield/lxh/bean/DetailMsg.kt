package com.mredrock.cyxbs.ufield.lxh.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class DetailMsg(
    @SerializedName("activity_cover_url")
    val activityCoverUrl: String, // http://xxx.jpg
    @SerializedName("activity_create_timestamp")
    val activityCreateTimestamp: Long, // 1691246775
    @SerializedName("activity_creator")
    val activityCreator: String, // 郝尧
    @SerializedName("activity_detail")
    val activityDetail: String, // 详细信息
    @SerializedName("activity_end_at")
    val activityEndAt: Long, // 1691246775
    @SerializedName("activity_id")
    val activityId: Int, // 35
    @SerializedName("activity_organizer")
    val activityOrganizer: String, // 软件工程学院
    @SerializedName("activity_place")
    val activityPlace: String, // 坟头
    @SerializedName("activity_registration_type")
    val activityRegistrationType: String, // ticket
    @SerializedName("activity_start_at")
    val activityStartAt: Long, // 1691246775
    @SerializedName("activity_title")
    val activityTitle: String, // 活动标题2
    @SerializedName("activity_type")
    val activityType: String, // culture
    @SerializedName("activity_watch_number")
    val activityWatchNumber: Int // 15
) : Serializable