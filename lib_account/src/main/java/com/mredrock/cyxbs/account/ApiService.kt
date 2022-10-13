package com.mredrock.cyxbs.account

import com.mredrock.cyxbs.account.bean.LoginParams
import com.mredrock.cyxbs.account.bean.RefreshParams
import com.mredrock.cyxbs.account.bean.TokenWrapper
import com.mredrock.cyxbs.account.bean.UserInfo
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import retrofit2.Call
import retrofit2.http.*

/**
 * Created By jay68 on 2018/8/10.
 */
internal interface ApiService {
    @POST("/magipoke/token")
    fun login(@Body loginParams: LoginParams): Call<ApiWrapper<TokenWrapper>>

    @POST("/magipoke/token/refresh")
    fun refresh(@Body refreshParams: RefreshParams,@Header("STU-NUM") stu:String): Call<ApiWrapper<TokenWrapper>>

    @GET("/magipoke/person/info")
    fun getUserInfo(@Header("Authorization") token: String): Call<ApiWrapper<UserInfo>>
}