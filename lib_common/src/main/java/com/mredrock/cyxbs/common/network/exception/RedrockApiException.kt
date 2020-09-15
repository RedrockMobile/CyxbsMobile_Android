package com.mredrock.cyxbs.common.network.exception

/**
 * Created By jay68 on 2018/8/12.
 */
open class RedrockApiException(message: String? = null, val status: Int = -1, cause: Throwable? = null)
    : RuntimeException(message ?: "RedrockApiException", cause)

class UnsetUserInfoException(message: String? = null, cause: Throwable? = null)
    : RedrockApiException(message ?: "UnsetUserInfoException", -1, cause)

class RedrockApiIllegalStateException : RedrockApiException("status equals 200 but data is null.")