package com.mredrock.cyxbs.common.service.account


/**
 * Created by yyfbe, Date on 2020-02-09.
 */
interface IUserTokenService {
    fun getRefreshToken(): String
    fun getToken(): String
}