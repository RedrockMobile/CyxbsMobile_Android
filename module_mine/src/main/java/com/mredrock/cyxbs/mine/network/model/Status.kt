package com.mredrock.cyxbs.mine.network.model
import java.io.Serializable

import com.google.gson.annotations.SerializedName


/**
 * @ClassName Status
 * @Description TODO
 * @Author 29942
 * @QQ 2994250239
 * @Date 2021/10/10 0:36
 * @Version 1.0
 */

data class Status(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("info")
    val info: String,
    @SerializedName("status")
    val status: Int
):Serializable

{
data class Data(
    @SerializedName("background")
    val background: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("form")
    val form: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("identityPic")
    val identityPic: String,
    @SerializedName("islate")
    val islate: Boolean,
    @SerializedName("isshow")
    val isshow: Boolean,
    @SerializedName("position")
    val position: String,
    @SerializedName("type")
    val type: String
):Serializable
}