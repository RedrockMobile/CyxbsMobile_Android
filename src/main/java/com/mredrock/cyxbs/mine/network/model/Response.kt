package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2020/2/8
 */
data class Response (
        @SerializedName("question_id")
        val questionId: Int,
        val answerId: Int,
        val commenterNickName: String,
        val commentContent: String
) : Serializable