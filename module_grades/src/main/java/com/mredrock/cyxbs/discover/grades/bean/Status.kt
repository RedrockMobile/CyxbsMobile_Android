package com.mredrock.cyxbs.discover.grades.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author: RayleighZ
 * Time: 2021-05-10 21:21
 * Describe: 是否使用H5页面代替掌邮页面的判断状态
 * ExamModel: magipoke/h5 (使用原生掌邮/使用前端H5网页代替)
 * Redirect: 前端网页url
 */
data class Status(
    @SerializedName("ExamModel")
    val examModel: String,
    @SerializedName("Redirect")
    val url: String
): Serializable