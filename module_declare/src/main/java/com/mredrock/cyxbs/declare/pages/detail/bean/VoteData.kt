package com.mredrock.cyxbs.declare.pages.detail.bean

/**
 * ... 投票的数据类
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/13
 * @Description:
 */
data class VoteData(
    var voted: String?,
    val choice: String,
    var percent: Int
)