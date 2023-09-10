package com.mredrock.cyxbs.declare.pages.post.bean

import com.google.gson.annotations.SerializedName

/**
 * com.mredrock.cyxbs.declare.pages.post.bean.PostReqBean.kt
 * CyxbsMobile_Android
 *
 * @author 寒雨
 * @since 2023/3/17 下午12:30
 */
data class PostReqBean(
    @SerializedName("title")
    val title: String,
    @SerializedName("choices")
    val choices: List<String>
)
