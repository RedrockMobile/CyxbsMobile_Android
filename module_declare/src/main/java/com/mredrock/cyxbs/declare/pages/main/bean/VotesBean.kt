package com.mredrock.cyxbs.declare.pages.main.bean

import com.google.gson.annotations.SerializedName

/**
 * ... 主页数据bean
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/4
 * @Description:
 */
data class VotesBean(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("Title")
    val title: String
    )