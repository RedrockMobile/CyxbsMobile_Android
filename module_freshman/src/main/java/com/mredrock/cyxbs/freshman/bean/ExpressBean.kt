package com.mredrock.cyxbs.freshman.bean

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import java.io.Serializable

data class ExpressBean(
    val code: Int,
    val text: List<ExpressText>
) : RedrockApiStatus(), Serializable

data class ExpressText(
    val message: List<ExpressMessage>,
    val name: String
):Serializable

data class ExpressMessage(
    var detail: String,
    var photo: String,
    val title: String
):Serializable