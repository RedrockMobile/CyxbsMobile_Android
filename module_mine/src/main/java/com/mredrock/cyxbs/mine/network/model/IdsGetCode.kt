package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 用ids修改密码时，获取验证码接口返回的数据类（验证码用于修改密码）
 */
data class IdsGetCode(
    @SerializedName("code")
    val code: Int
) : Serializable