package com.mredrock.cyxbs.mine.page.feedback.history.detail.bean

/**
 *@author ZhiQiang Tu
 *@time 2021/8/24  9:32
 *@signature 我们不明前路，却已在路上
 */
data class Feedback(
    val date: Long,
    val title: String,
    val content: String,
    val label: String,
    val urls: List<String>,
)