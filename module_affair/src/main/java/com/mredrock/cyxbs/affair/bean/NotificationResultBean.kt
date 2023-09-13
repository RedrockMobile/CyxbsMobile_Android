package com.mredrock.cyxbs.affair.bean

import com.mredrock.cyxbs.lib.utils.network.IApiStatus

/**
 * 发送通知post的结果bean类
 * data返回"limit"的话表示一天发送消息超过五次，禁止！
 */
data class NotificationResultBean(
    override val status: Int,
    override val info: String,
    val data : String
) : IApiStatus
