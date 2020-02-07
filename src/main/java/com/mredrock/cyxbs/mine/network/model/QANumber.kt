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