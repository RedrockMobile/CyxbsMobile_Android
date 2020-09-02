package com.mredrock.cyxbs.discover.grades.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2020/3/21
 */
data class IdsStatus(
        val status: String,
        @SerializedName("errcode")
        val errorCode: String,
        @SerializedName("errmessage")
        val errorMessage: String
) : Serializable