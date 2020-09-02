package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2019/12/1
 */
data class AskPosted(
        @SerializedName("question_id")
        val questionId: Int,
        @SerializedName("title")
        val title: String = "",
        @SerializedName("description")
        val description: String = "",
        @SerializedName("disappear_at")
        val disappearAt: String = "",
        @SerializedName("integral")
        val integral: Int = 10,
        @SerializedName("type")
        val type: String
) : Serializable
/**
"question_id": 1244,
"title": "abd",
"description": "aja",
"integral": 1,
"type": "未解决",
"disappear_at": "2020-02-15 12:40:00"
 */