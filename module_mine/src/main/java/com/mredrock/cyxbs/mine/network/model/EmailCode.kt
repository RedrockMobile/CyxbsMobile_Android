package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by wangtianqi on 2020/10/31
 * 接收的邮箱验证码
 */
data class EmailCode(
        @SerializedName("expired_time")
        val expiredTime: Int
) : Serializable