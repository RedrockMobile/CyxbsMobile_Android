package com.mredrock.cyxbs.ufield.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ActivityBean(
    @SerializedName("activity_cover_url")
    val activityCoverUrl: String,
    @SerializedName("activity_create_timestamp")
    val activityCreateTimestamp: Int,
    @SerializedName("activity_creator")
    val activityCreator: String,
    @SerializedName("activity_detail")
    val activityDetail: String,
    @SerializedName("activity_end_at")
    val activityEndAt: Long,
    @SerializedName("activity_id")
    val activityId: Int,
    @SerializedName("activity_organizer")
    val activityOrganizer: String,
    @SerializedName("activity_place")
    val activityPlace: String,
    @SerializedName("activity_registration_type")
    val activityRegistrationType: String,
    @SerializedName("activity_start_at")
    val activityStartAt: Long,
    @SerializedName("activity_title")
    val activityTitle: String,
    @SerializedName("activity_type")
    val activityType: String,
    @SerializedName("activity_watch_number")
    val activityWatchNumber: Int,
    @SerializedName("ended")
    val ended: Boolean,
    @SerializedName("want_to_watch")
    val wantToWatch: Boolean,
    @SerializedName("activity_state")
    val activityState:String,
    @SerializedName("is_add")
    val isAdd:Boolean
):Serializable

