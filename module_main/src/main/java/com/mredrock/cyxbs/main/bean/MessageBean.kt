package com.mredrock.cyxbs.main.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * description ： TODO:类的作用
 * author : 苟云东
 * email : 2191288460@qq.com
 * date : 2023/8/27 11:33
 */
data class MessageBean(
    @SerializedName("nickname")
    val nickname:String,
    @SerializedName("photo_src")
    val photo_src:String
):Serializable
