package com.mredrock.cyxbs.declare.pages.detail.bean

/**
 * ... 投完票的数据bean
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/13
 * @Description:
 */
data class VotedBean(
    val statistic: Map<String,Int>,
    val voted: String
)