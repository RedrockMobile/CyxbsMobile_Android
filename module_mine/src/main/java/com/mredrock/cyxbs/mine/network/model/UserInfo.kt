package com.mredrock.cyxbs.mine.network.model
import java.io.Serializable

import com.google.gson.annotations.SerializedName



data class UserInfo(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("info")
    val info: String,
    @SerializedName("status")
    val status: Double
):Serializable

