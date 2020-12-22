package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *@Date 2020-11-02
 *@Time 21:17
 *@author SpreadWater
 *@description 用于接收是否绑定信息的基类
 */
class BindingResponse(
        @SerializedName("question_is")
        val question_is: Int,
        @SerializedName("email_is")
        val email_is: Int
) : Serializable