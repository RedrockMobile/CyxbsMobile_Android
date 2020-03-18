package com.mredrock.cyxbs.qa.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Comment(@SerializedName("id")
                   val id: String = "",
                   @SerializedName("stunum")
                   val stuNum: String = "",
                   @SerializedName("photo_thumbnail_src")
                   val photoThumbnailSrc: String = "",
                   @SerializedName("gender")
                   val gender: String = "",
                   @SerializedName("nickname")
                   val nickname: String = "",
                   @SerializedName("created_at")
                   val createdAt: String = "",
                   @SerializedName("content")
                   val content: String = "") : Serializable {
}