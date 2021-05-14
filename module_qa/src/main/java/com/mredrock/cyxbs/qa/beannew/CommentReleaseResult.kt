package com.mredrock.cyxbs.qa.beannew

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CommentReleaseResult(
        @SerializedName("comment_id") val commentId: Int = 0,
        @SerializedName("post_id") val postId: String = ""
) : Serializable