package com.mredrock.cyxbs.mine.page.feedback.network.bean

/**
 *@author ZhiQiang Tu
 *@time 2021/9/3  10:46
 *@signature 我们不明前路，却已在路上
 */

import com.google.gson.annotations.SerializedName
data class HistoryDetail(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data2,
    @SerializedName("info")
    val info: String
)

data class Data2(
    @SerializedName("feedback")
    val feedback: Feedback,
    @SerializedName("reply")
    val reply: Any
)

data class Feedback(
    @SerializedName("content")
    val content: String,
    @SerializedName("CreatedAt")
    val createdAt: String,
    @SerializedName("DeletedAt")
    val deletedAt: Any,
    @SerializedName("ID")
    val iD: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("pictures")
    val pictures: List<String> ?,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("replied")
    val replied: Boolean,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("UpdatedAt")
    val updatedAt: String
)