package com.mredrock.cyxbs.freshman.bean

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import java.io.Serializable

data class SexRatioBean(
        val code: Int,
        val text: List<SexRatioText>,
        val title: String
) : RedrockApiStatus(), Serializable

data class SexRatioText(
    val boy: String,
    val girl: String,
    val name: String
):Serializable
