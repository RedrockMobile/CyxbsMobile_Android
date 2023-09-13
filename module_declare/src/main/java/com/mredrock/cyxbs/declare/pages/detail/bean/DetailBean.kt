package com.mredrock.cyxbs.declare.pages.detail.bean

import com.google.gson.annotations.SerializedName

/**
 * ... 详细投票数据的bean
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/4
 * @Description:
 */
data class DetailBean(
    @SerializedName("choices")
    val choices: List<String>?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("statistic")
    val statistic: Map<String, Int>?,
    @SerializedName("title")
    val title: String,
    @SerializedName("voted")
    val voted: String?
)