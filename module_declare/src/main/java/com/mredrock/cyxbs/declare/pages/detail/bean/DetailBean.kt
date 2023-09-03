package com.mredrock.cyxbs.declare.pages.detail.bean

/**
 * ... 详细投票数据的bean
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/4
 * @Description:
 */
data class DetailBean(
    val choices: List<String>?,
    val id: Int,
    val statistic: Map<String, Int>?,
    val title: String,
    val voted: String?
)