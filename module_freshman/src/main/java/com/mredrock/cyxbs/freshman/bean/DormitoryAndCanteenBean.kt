package com.mredrock.cyxbs.freshman.bean

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import java.io.Serializable

/**
 * Create by yuanbing
 * on 2019/8/7
 */
data class DormitoryAndCanteenBean(
    val code: Int,
    val text: List<DormitoryAndCanteenText>
) : RedrockApiStatus(), Serializable

data class DormitoryAndCanteenText(
        val message: List<DormitoryAndCanteenMessage>,
        val title: String
):Serializable

data class DormitoryAndCanteenMessage(
    var detail: String,
    val name: String,
    var photo: List<String>
):Serializable