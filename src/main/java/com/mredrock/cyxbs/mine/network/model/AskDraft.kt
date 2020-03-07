package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2020/2/11
 */
data class AskDraft(
        @SerializedName("draft_question_id")
        val draftQuestionId: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("latest_edit_time")
        val latestEditTime: String,
        @SerializedName("content")
        val content: String
) : Serializable