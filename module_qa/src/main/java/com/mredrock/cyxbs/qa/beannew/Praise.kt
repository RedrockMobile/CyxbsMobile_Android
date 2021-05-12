package com.mredrock.cyxbs.qa.beannew

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @date 2021-03-02
 * @author Sca RayleighZ
 */
data class Praise(
        @SerializedName("post_id")
        val id: String,
        @SerializedName("type")
        val type: Int,
        @SerializedName("time")
        val time: String,
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("nick_name")
        val nickName: String,
        @SerializedName("from")
        val from: String
) : Serializable