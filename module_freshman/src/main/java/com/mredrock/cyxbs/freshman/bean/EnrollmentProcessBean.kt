package com.mredrock.cyxbs.freshman.bean

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import java.io.Serializable

/**
 * Create by yuanbing
 * on 2019/8/3
 */
data class EnrollmentProcessBean(
    val code: Int,
    val text: List<EnrollmentProcessText>
) : RedrockApiStatus(), Serializable

data class EnrollmentProcessText(
    var detail: String,
    val message: String,
    var photo: String,
    val title: String
):Serializable