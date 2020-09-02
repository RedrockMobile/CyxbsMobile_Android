package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2019/12/3
 */
data class AnswerPosted(
        @SerializedName("answer_id")
        val answerId: Int,
        @SerializedName("question_id")
        val questionId: Int,
        @SerializedName("answer_content")
        val content: String,
        @SerializedName("answer_time")
        val answerTime: String,
        @SerializedName("integral")
        val integral: Int,
        @SerializedName("type")
        val type: String

) : Serializable