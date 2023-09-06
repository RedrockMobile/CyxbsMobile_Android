package com.mredrock.cyxbs.ufield.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *  description :全部 文娱 教育 体育 活动 使用相同的数据接受类
 *  author : lytMoon
 *  date : 2023/8/20 18:05
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */
data class ItemActivityBean(
    @SerializedName("ended")
    var ended: List<ItemAll>,
    @SerializedName("ongoing")
    var ongoing: List<ItemAll>
) : Serializable {
    data class ItemAll(
        @SerializedName("activity_cover_url")
        val activity_cover_url: String,
        @SerializedName("activity_create_timestamp")
        val activity_create_timestamp: Long,
        @SerializedName("activity_creator")
        val activity_creator: String,
        @SerializedName("activity_detail")
        val activity_detail: String,
        @SerializedName("activity_end_at")
        val activity_end_at: Long,
        @SerializedName("activity_id")
        val activity_id: Int,
        @SerializedName("activity_organizer")
        val activity_organizer: String,
        @SerializedName("activity_place")
        val activity_place: String,
        @SerializedName("activity_registration_type")
        val activity_registration_type: String,
        @SerializedName("activity_start_at")
        val activity_start_at: Long,
        @SerializedName("activity_title")
        val activity_title: String,
        @SerializedName("activity_type")
        val activity_type: String,
        @SerializedName("activity_watch_number")
        val activity_watch_number: Int,
        @SerializedName("want_to_watch")
        val want_to_watch: Boolean,
        @SerializedName("ended")
        val ended: Boolean
    ) : Serializable


}



