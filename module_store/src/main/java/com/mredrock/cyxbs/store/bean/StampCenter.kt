package com.mredrock.cyxbs.store.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StampCenter(
    @SerializedName("shop")
    val shop: List<Shop>?,
    @SerializedName("task")
    val task: List<Task>?,
    @SerializedName("un_got_good")
    val unGotGood: Boolean,
    @SerializedName("user_amount")
    val userAmount: Int
) : Serializable {
    data class Shop(
        @SerializedName("amount")
        val amount: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("price")
        val price: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("type")
        val type: Int,
        @SerializedName("url")
        val url: String,
        @SerializedName("is_purchased")
        val isPurchased: Boolean
    ) : Serializable

    data class Task(
        @SerializedName("current_progress")
        val currentProgress: Int,
        @SerializedName("description")
        val description: String,
        @SerializedName("gain_stamp")
        val gainStamp: Int,
        @SerializedName("max_progress")
        val maxProgress: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("type")
        val type: String
    ) : Serializable
}