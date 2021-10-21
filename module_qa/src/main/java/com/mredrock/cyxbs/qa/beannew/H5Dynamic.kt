package com.mredrock.cyxbs.qa.beannew

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class H5Dynamic(
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("nick_name")
        val nickName: String,
        @SerializedName("annotation")
        val annotation: String,
        @SerializedName("pic")
        val pic: String,
        @SerializedName("link_url")
        val linkUrl: String
): Serializable, Message()
