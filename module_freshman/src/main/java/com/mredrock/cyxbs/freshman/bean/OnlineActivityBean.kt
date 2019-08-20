package com.mredrock.cyxbs.freshman.bean

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import java.io.Serializable

/**
 * Create by yuanbing
 * on 2019/8/3
 */
data class OnlineActivityBean(
    val code: Int,
    val text: List<OnlineActivityText>
) : RedrockApiStatus(), Serializable

data class OnlineActivityText(
    var QR: String,
    val message: String,
    val name: String,
    var photo: String
):Serializable
