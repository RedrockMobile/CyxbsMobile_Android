package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RelateMeItem(@SerializedName("photo_thumbnail_src")
                        val photoThumbnailSrc: String = "",
                        @SerializedName("answer_content")
                        val answerContent: String = "",
                        @SerializedName("nickname")
                        val nickname: String = "",
                        @SerializedName("created_at")
                        val createdAt: String = "",
                        @SerializedName("target_id")
                        val targetId: String = "",
                        @SerializedName("type")
                        val type: String = "",
                        @SerializedName("question_id")
                        val questionId: String = "",
                        @SerializedName("content")
                        val content: String = "",
                        @SerializedName("photo_src")
                        val photoSrc: String = ""):Serializable {
    val typeDescription get() = (if (type == "1") "赞" else "评论")
    val isComment get() = type == "2"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RelateMeItem

        if (targetId != other.targetId) return false
        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int {
        var result = targetId.hashCode()
        result = 31 * result + content.hashCode()
        return result
    }
}