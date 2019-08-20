package com.mredrock.cyxbs.freshman.bean

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import java.io.Serializable

/**
 * Create by yuanbing
 * on 2019/8/8
 */
data class EnrollmentRequirementsBean(
    val code: Int,
    val text: List<EnrollmentRequirementsText>
) : RedrockApiStatus(), Serializable

data class EnrollmentRequirementsText(
        val `data`: List<EnrollmentRequirementsData>,
        val title: String
):Serializable

data class EnrollmentRequirementsData(
    val detail: String,
    val name: String
):Serializable