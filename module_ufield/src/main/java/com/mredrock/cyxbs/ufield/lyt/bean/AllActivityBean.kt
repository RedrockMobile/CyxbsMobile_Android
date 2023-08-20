package com.mredrock.cyxbs.ufield.lyt.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *  description :
 *  author : lytMoon
 *  date : 2023/8/20 18:05
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */
//data class AllActivityBean(
//    val `data`: Data,
//)
//
//data class Data(
//    val ended: List<Ended>,
//    val ongoing: List<Ongoing>
//)
//
//data class Ended(
//    val activity_cover_url: String,
//    val activity_create_timestamp: Int,
//    val activity_creator: String,
//    val activity_detail: String,
//    val activity_end_at: Int,
//    val activity_id: Int,
//    val activity_organizer: String,
//    val activity_place: String,
//    val activity_registration_type: String,
//    val activity_start_at: Int,
//    val activity_title: String,
//    val activity_type: String,
//    val activity_watch_number: Int,
//    val ended: Boolean,
//    val want_to_watch: Boolean
//)
//
//data class Ongoing(
//    val activity_cover_url: String,
//    val activity_create_timestamp: Int,
//    val activity_creator: String,
//    val activity_detail: String,
//    val activity_end_at: Long,
//    val activity_id: Int,
//    val activity_organizer: String,
//    val activity_place: String,
//    val activity_registration_type: String,
//    val activity_start_at: Int,
//    val activity_title: String,
//    val activity_type: String,
//    val activity_watch_number: Int,
//    val ended: Boolean,
//    val want_to_watch: Boolean
//)


data class AllActivityBean(
    @SerializedName("ended")
    var ended: List<ItemAll>,
    @SerializedName("ongoing")
    var ongoing: List<ItemAll>
) : Serializable {
    data class ItemAll(
        @SerializedName("activity_cover_url")
        val activity_cover_url: String,
        @SerializedName("activity_create_timestamp")
        val activity_create_timestamp: Int,
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
        val activity_start_at: Int,
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




//    fun getMergedList(): List<MergedAllBean> {
//        val mergedList = mutableListOf<MergedAllBean>()
//        for (it in ongoing) {
//            mergedList.add(MergedAllBean(it, "ongoing"))
//        }
//
//
//    }


}



