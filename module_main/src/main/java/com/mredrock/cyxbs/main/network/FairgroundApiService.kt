package com.mredrock.cyxbs.main.network

import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.lib.utils.network.IApi
import com.mredrock.cyxbs.main.bean.DaysBean
import com.mredrock.cyxbs.main.bean.MessageBean
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

/**
 *
 * author : 苟云东
 * email : 2191288460@qq.com
 * date : 2023/8/26 17:21
 */
interface FairgroundApiService :IApi{

    @GET("/magipoke-playground/center/days")
    fun getDays():Single<ApiWrapper<DaysBean>>

    @GET("/magipoke/person/info")
    fun getMessage():Single<ApiWrapper<MessageBean>>
}