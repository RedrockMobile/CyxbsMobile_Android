package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2020/2/15
 */
data class MyProduct(
        @SerializedName("name")
        val name: String,
        @SerializedName("num")
        val count: Int,
        @SerializedName("value")
        val integral: Int,
        @SerializedName("time")
        val time: String,
        @SerializedName("isReceived")
        val isReceived: Int,
        @SerializedName("isVirtual")
        val isVirtual: Int,
        @SerializedName("photo_src")
        val photoSrc: String
) : Serializable