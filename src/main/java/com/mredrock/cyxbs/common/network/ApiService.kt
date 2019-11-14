package com.mredrock.cyxbs.common.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.bean.TokenBean
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.Serializable

/**
 * Create by roger
 * on 2019/11/13
 */
interface ApiService {
    @POST("/app/token/refresh")
    fun refreshToken(@Body token: RefreshToken): Observable<RedrockApiWrapper<TokenBean>>
}

data class RefreshToken(
        val refreshToken: String
) : Serializable
