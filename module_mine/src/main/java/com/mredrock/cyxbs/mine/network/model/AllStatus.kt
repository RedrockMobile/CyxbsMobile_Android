package com.mredrock.cyxbs.mine.network.model
import java.io.Serializable

import com.google.gson.annotations.SerializedName


/**
 * @ClassName AllStatus
 * @Description TODO
 * @Author 29942
 * @QQ 2994250239
 * @Date 2021/10/17 20:27
 * @Version 1.0
 */

data class AllStatus(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("info")
    val info: String,
    @SerializedName("status")
    val status: Int
) :Serializable{

    data class Data(
        @SerializedName("authentication")
        val authentication: List<AuthenticationStatus.Data>,
        @SerializedName("customization")
        val customization: List<AuthenticationStatus.Data>
    ):Serializable
}


