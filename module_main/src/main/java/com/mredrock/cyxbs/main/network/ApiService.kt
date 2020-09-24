package com.mredrock.cyxbs.main.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.main.bean.StartPage
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created By jay68 on 2018/8/10.
 */
interface ApiService {
    @GET("/wxapi/magipoke-text/start/photo")
    fun getStartPage(): Observable<RedrockApiWrapper<List<StartPage>>>
}