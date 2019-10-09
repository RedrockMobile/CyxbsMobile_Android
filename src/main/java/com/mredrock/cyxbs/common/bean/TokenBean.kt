package com.mredrock.cyxbs.common.bean

import java.io.Serializable

/**
 * Create by roger
 * on 2019/10/4
 */
data class TokenBean(
        val refreshToken: String?,
        val token: String?
) : Serializable