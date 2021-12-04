package com.mredrock.cyxbs.qa.beannew


import com.google.gson.annotations.SerializedName

data class UserBrief(
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("is_focus")
    val isFocus: Boolean,
    @SerializedName("is_be_focused")
    val isBeFocused: Boolean,
    @SerializedName("introduction")
    val introduction: String,
    @SerializedName("stuNum")
    val stuNum: Long,
    @SerializedName("redid")
    val redid: String
)