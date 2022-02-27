package com.mredrock.cyxbs.mine.network.model
import java.io.Serializable

import com.google.gson.annotations.SerializedName


/**
 * @ClassName PersonalStatu
 * @Description TODO
 * @Author 29942
 * @QQ 2994250239
 * @Date 2021/10/18 21:17
 * @Version 1.0
 */

data class PersonalStatu(
    @SerializedName("data")
    val `data`: Status.Data,
    @SerializedName("info")
    val info: String,
    @SerializedName("status")
    val status: Int
) :Serializable