package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2020/2/7
 */
data class QANumber(
        @SerializedName("answer_posted_number")
        val answerPostedNumber: Int,
        @SerializedName("ask_posted_number")
        val askPostedNumber: Int,
        @SerializedName("comment_number")
        val commentNumber: Int,
        @SerializedName("praise_number")
        val praiseNumber: Int
) : Serializable
/**
         "ask_posted_number": 1,         用户的提问已发布的总数
        "answer_posted_number": 6,      用户的回答已发布的总数
        "praise_number": 1,             用户获赞数
        "comment_number": 8             评论数（评论数 = 收到的评论 + 发出的评论）
 */