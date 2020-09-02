package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2019/12/8
 * 发出的评论
 */
data class Comment (
        @SerializedName("question_id")
        val questionId: Int,
        @SerializedName("answer_id")
        val answerId: Int,
        @SerializedName("comment_content")
        val commentContent: String,
        @SerializedName("answerer")
        val answerer: String
) : Serializable