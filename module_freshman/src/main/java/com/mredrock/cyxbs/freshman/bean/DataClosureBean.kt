package com.mredrock.cyxbs.freshman.bean

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import java.io.Serializable

/**
 * Create by yuanbing
 * on 2019/8/2
 */
data class DataClosureBean(
    val code: Int,
    val text: List<DataClosureText>
) : RedrockApiStatus(), Serializable

data class DataClosureText(
    val message: List<DataClosureMessage>,
    val name: String
):Serializable

data class DataClosureMessage(
    val `data`: List<DataClosureData>,
    val boy: String,
    val girl: String,
    val title: String
):Serializable

data class DataClosureData(
    val `data`: String,
    val subject_1: String,
    val subject_2: String
):Serializable