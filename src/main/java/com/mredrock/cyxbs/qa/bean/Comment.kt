package com.mredrock.cyxbs.qa.bean

import com.google.gson.annotations.SerializedName

data class Comment(@SerializedName("photo_thumbnail_src")
                   val photoThumbnailSrc: String = "",
                   @SerializedName("gender")
                   val gender: String = "",
                   @SerializedName("nickname")
                   val nickname: String = "",
                   @SerializedName("created_at")
                   val createdAt: String = "",
                   @SerializedName("content")
                   val content: String = "") {
    val isMale get() = gender != "女"
}