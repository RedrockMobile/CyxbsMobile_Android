package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2020/2/10
 * 收到的评论
 */
data class CommentReceived(
        @SerializedName("question_id")
        val questionId: Int,
        @SerializedName("answer_id")
        val answerId: Int,
        @SerializedName("comment_content")
        val commentContent: String,
        @SerializedName("commenter_nickname")
        val commenterNickname: String,
        @SerializedName("commenter_imageurl")
        val commenterImageUrl: String
) : Serializable