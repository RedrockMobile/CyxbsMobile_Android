package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2020/2/11
 */
data class AnswerDraft(
        @SerializedName("draft_answer_id")
        val draftAnswerId: Int,
        @SerializedName("question_id")
        val questionId: String,
        @SerializedName("latest_edit_time")
        val latestEditTime: String,
        @SerializedName("draft_answer_content")
        val draftAnswerContent: String
) : Serializable