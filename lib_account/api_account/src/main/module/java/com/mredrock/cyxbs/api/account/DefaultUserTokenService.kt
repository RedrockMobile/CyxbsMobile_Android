package com.mredrock.cyxbs.api.account

/**
 *@author ZhiQiang Tu
 *@time 2022/4/2  16:00
 *@signature 我将追寻并获取我想要的答案
 */
class DefaultUserTokenService : IUserTokenService {
    override fun getRefreshToken(): String = defaultString

    override fun getToken(): String = defaultString
}