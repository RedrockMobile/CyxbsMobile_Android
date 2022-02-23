package com.mredrock.cyxbs.mine.network.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Fan(
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("is_focus")
    val isFocus: Boolean,
    @SerializedName("introduction")
    val introduction: String,
    @SerializedName("stuNum")
    val stuNum: String,
    @SerializedName("redid")
    val redid: String
):Serializable

