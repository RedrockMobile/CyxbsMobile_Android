package com.mredrock.cyxbs.qa.beannew

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @date 2021-03-02
 * @author Sca RayleighZ
 * 含有form（被回复的文本）的comment包装类
 */
data class CommentWrapper(
        @SerializedName("from")
        val from: String,
        @SerializedName("comment")
        val comment: Comment,
        @SerializedName("type")
        val type: Int
): Serializable
