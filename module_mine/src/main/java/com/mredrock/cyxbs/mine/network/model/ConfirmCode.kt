package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author: RayleighZ
 * Time: 2020-11-03 21:54
 * Describe: 邮箱验证时返回的认证码
 */
class ConfirmCode(
        @SerializedName("code")
        val code : String,
        @SerializedName("expire_time")
        val expire_time : Int
) : Serializable