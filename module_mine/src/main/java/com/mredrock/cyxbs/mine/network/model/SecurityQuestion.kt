package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @date 2020-10-29
 * @author Sca RayleighZ
 * describe: 密保问题的bean类，网络请求用
 */
data class SecurityQuestion(
        @SerializedName("id")
        val id: Int,
        @SerializedName("content")
        val content: String
) : Serializable