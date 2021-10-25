package com.mredrock.cyxbs.mine.network.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * @ClassName PersonalCount
 * @Description TODO
 * @Author 29942
 * @QQ 2994250239
 * @Date 2021/10/24 17:08
 * @Version 1.0
 */

data class PersonalCount(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("info")
    val info: String,
    @SerializedName("status")
    val status: Int
) :Serializable{

    data class Data(
        @SerializedName("comment")
        val comment: Int,
        @SerializedName("dynamic")
        val `dynamic`: Int,
        @SerializedName("fans")
        val fans: Int,
        @SerializedName("follows")
        val follows: Int,
        @SerializedName("praise")
        val praise: Int
    )
}
