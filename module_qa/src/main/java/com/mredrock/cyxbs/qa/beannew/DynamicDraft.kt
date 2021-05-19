package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by xgl on 2021/3/16
 */
data class DynamicDraft(
        @SerializedName("isDraft")
        val isDraft: Int,
        @SerializedName("content")
        val content: String,
        @SerializedName("images")
        var images: List<String>,
        @SerializedName("type")
        val type: String
) : Serializable