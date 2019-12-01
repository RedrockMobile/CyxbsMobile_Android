package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2019/12/1
 */
data class AskPosted(
        @SerializedName("question_id")
        val id: String = "",
        @SerializedName("title")
        val title: String = "",
        @SerializedName("description")
        val description: String = "",
        @SerializedName("disappear_at")
        val disappearAt: String = "",
        @SerializedName("created_at")
        val createdAt: String = "",
        @SerializedName("updated_at")
        val updatedAt: String = "",
        val integral: Int = 10,
        val state: Int = 0
) : Serializable