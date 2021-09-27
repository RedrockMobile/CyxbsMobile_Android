package com.mredrock.cyxbs.qa.beannew


import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("info")
    val info: String,
    @SerializedName("status")
    val status: Int
) {
    data class Data(
        @SerializedName("users")
        val users: List<User>
    ) {
        data class User(
            @SerializedName("id")
            val id: String,
            @SerializedName("avatar")
            val avatar: String,
            @SerializedName("nickname")
            val nickname: String,
            @SerializedName("isFocus")
            val isFocus: Boolean,
            @SerializedName("introduction")
            val introduction: String,
            @SerializedName("stuNum")
            val stuNum: String
        )
    }
}