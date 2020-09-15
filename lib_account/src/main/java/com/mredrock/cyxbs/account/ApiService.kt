package com.mredrock.cyxbs.account

import com.mredrock.cyxbs.account.bean.LoginParams
import com.mredrock.cyxbs.account.bean.RefreshParams
import com.mredrock.cyxbs.account.bean.TokenWrapper
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created By jay68 on 2018/8/10.
 */
internal interface ApiService {
    @POST("/app/token")
    fun login(@Body loginParams: LoginParams): Call<RedrockApiWrapper<TokenWrapper>>

    @POST("/app/token/refresh")
    fun refresh(@Body refreshParams: RefreshParams): Call<RedrockApiWrapper<TokenWrapper>>
}