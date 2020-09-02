package com.mredrock.cyxbs.discover.grades.bean.analyze

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2020/3/21
 */
data class GPAStatus(
        val data: GPAData,
        val status: String,
        @SerializedName("errcode")
        val errorCode: String,
        @SerializedName("errmessage")
        val errorMessage: String
) : Serializable

val GPAStatus.isSuccessful get() = (status == "10000")
//说明没有绑定
val GPAStatus.isNotBind get() = (errorCode == "10010")