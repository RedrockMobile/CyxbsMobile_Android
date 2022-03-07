package com.mredrock.cyxbs.mine.page.feedback.network.bean.history

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
    val `data`: Data,
    @SerializedName("info")
    val info: String
)

data class Data(
    @SerializedName("feedback")
    val feedback: Feedback,
    @SerializedName("reply")
    val reply: Reply?
)

data class Feedback(
    @SerializedName("content")
    val content: String,
    @SerializedName("CreatedAt")
    val createdAt: String,
    @SerializedName("DeletedAt")
    val deletedAt: String,
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

data class Reply(
    @SerializedName("content")
    val content: String,
    @SerializedName("CreatedAt")
    val createdAt: String,
    @SerializedName("DeletedAt")
    val deletedAt: String,
    @SerializedName("feedback_id")
    val feedbackId: Int,
    @SerializedName("ID")
    val iD: Long,
    @SerializedName("UpdatedAt")
    val updatedAt: String,
    @SerializedName("urls")
    val urls: List<String>?
)