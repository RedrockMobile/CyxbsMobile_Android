package com.mredrock.cyxbs.qa.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Comment(@SerializedName("id")
                   val id: String = "",
                   @SerializedName("is_self")
                   private val _isSelf: Int = 0,
                   @SerializedName("photo_thumbnail_src")
                   val photoThumbnailSrc: String = "",
                   @SerializedName("gender")
                   val gender: String = "",
                   @SerializedName("nickname")
                   val nickname: String = "",
                   @SerializedName("created_at")
                   val createdAt: String = "",
                   @SerializedName("content")
                   val content: String = "",
                   @SerializedName("Question_is_self")
                   private val _questionIsSelf: Int = 0) : Serializable {
    val isSelf get() = _isSelf == 1
    val questionIsSelf get() = _questionIsSelf == 1
}