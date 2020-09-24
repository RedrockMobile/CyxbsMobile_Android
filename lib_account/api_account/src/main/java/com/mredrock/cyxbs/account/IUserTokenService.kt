package com.mredrock.cyxbs.account


/**
 * Created by yyfbe, Date on 2020-02-09.
 */
interface IUserTokenService {
    fun getRefreshToken(): String
    fun getToken(): String
}