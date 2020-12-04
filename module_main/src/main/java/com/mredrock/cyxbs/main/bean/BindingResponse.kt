package com.mredrock.cyxbs.main.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author: RayleighZ
 * Time: 2020-12-05 1:30
 * describe: 检查用户是否绑定邮箱和密码的返回值
 */
class BindingResponse(
        @SerializedName("question_is")
        val question_is: Int,
        @SerializedName("email_is")
        val email_is: Int
) : Serializable