package com.mredrock.cyxbs.declare.pages.detail.bean

import com.google.gson.annotations.SerializedName

/**
 * ... 投完票的数据bean
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/13
 * @Description:
 */
data class VotedBean(
    @SerializedName("statistic")
    val statistic: Map<String,Int>,
    @SerializedName("voted")
    val voted: String
)