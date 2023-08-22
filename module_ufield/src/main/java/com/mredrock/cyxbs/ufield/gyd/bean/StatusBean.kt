package com.mredrock.cyxbs.ufield.gyd.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * description ： TODO:类的作用
 * author : 苟云东
 * email : 2191288460@qq.com
 * date : 2023/8/21 17:36
 */
data class StatusBean(
    @SerializedName("data")
    val status:String=""
):Serializable
