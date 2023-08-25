package com.mredrock.cyxbs.ufield.lxh.bean
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class RankBean(
    @SerializedName("activity_cover_url")
    val activityCoverUrl: String, // http://xxx.jpg
    @SerializedName("activity_create_timestamp")
    val activityCreateTimestamp: Long, // 1691241359
    @SerializedName("activity_creator")
    val activityCreator: String, // 郝尧
    @SerializedName("activity_detail")
    val activityDetail: String, // 这是军事演习, 大家都来捧场哈
    @SerializedName("activity_end_at")
    val activityEndAt: Long, // 1691241360
    @SerializedName("activity_id")
    val activityId: Int, // 19
    @SerializedName("activity_organizer")
    val activityOrganizer: String, // 软件工程学院
    @SerializedName("activity_place")
    val activityPlace: String, // 风雨操场
    @SerializedName("activity_registration_type")
    val activityRegistrationType: String, // free
    @SerializedName("activity_start_at")
    val activityStartAt: Long, // 1691241359
    @SerializedName("activity_title")
    val activityTitle: String, // 军事演习
    @SerializedName("activity_type")
    val activityType: String, // education
    @SerializedName("activity_watch_number")
    val activityWatchNumber: Int // 3
) : Serializable