package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2019/12/3
 */
data class AnswerPosted (
        @SerializedName("id")
        val id: String,
        @SerializedName("question_id")
        val questionId: String,
        @SerializedName("content")
        val content: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("question_title")
        val questionTitle: String,
        @SerializedName("disappear_at")
        val disappearAt: String,
        @SerializedName("updated_at")
        val upDatedAt: String,
        val integral: Int,
        val state: Int = 0

) : Serializable