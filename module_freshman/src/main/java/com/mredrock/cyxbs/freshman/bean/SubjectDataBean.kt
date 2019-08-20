package com.mredrock.cyxbs.freshman.bean

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import java.io.Serializable

/**
 * Create by yuanbing
 * on 2019/8/7
 */
data class SubjectDataBean(
        val code: Int,
        val text: List<SubjectDataText>,
        val title: String
) : RedrockApiStatus(), Serializable

data class SubjectDataText(
        val message: List<SubjectDataMessage>,
        val name: String
):Serializable

data class SubjectDataMessage(
    val `data`: String,
    val subject: String
):Serializable
