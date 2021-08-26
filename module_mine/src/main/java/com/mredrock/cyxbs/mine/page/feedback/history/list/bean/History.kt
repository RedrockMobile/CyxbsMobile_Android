package com.mredrock.cyxbs.mine.page.feedback.history.list.bean

/**
 *@author ZhiQiang Tu
 *@time 2021/8/24  10:37
 *@signature 我们不明前路，却已在路上
 */
data class History(
    val title: String,
    val date: Long,
    val replyOrNot: Boolean,
    var isRead: Boolean,
    val id: Long
)