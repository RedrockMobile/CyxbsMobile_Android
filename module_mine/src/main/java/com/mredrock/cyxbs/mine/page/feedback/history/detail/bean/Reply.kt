package com.mredrock.cyxbs.mine.page.feedback.history.detail.bean

/**
 *@author ZhiQiang Tu
 *@time 2021/8/24  9:49
 *@signature 我们不明前路，却已在路上
 */
data class Reply(
    val date: Long,
    val content: String,
    val isReply:Boolean
)