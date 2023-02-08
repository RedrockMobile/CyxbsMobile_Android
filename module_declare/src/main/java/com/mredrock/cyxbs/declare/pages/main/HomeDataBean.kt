package com.mredrock.cyxbs.declare.pages.main

import com.google.gson.annotations.SerializedName

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/4
 * @Description:
 */
data class HomeDataBean(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("Title")
    val title: String
    )