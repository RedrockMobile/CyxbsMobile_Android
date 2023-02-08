package com.mredrock.cyxbs.declare.pages.main

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/4
 * @Description:
 */
data class DeclareDetailBean(
    val choices: List<String>,
    val id: Int,
    val statistic: Map<String, Int>,
    val title: String,
    val voted: String
)