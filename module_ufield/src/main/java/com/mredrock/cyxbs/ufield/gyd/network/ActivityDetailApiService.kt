package com.mredrock.cyxbs.ufield.gyd.network

import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.ufield.gyd.bean.ActivityBean
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

/**
 * description ： TODO:类的作用
 * author : 苟云东
 * email : 2191288460@qq.com
 * date : 2023/8/24 11:19
 */
interface ActivityDetailApiService {
    companion object {
        val INSTANCE by lazy { ApiGenerator.getApiService(ActivityDetailApiService::class) }
    }

    @GET("/magipoke-ufield/activity/")
    fun getActivityData(@Query("activity_id")id:Int):Single<ApiWrapper<ActivityBean>>

    @PUT("/magipoke-ufield/activity/action/watch/")
    fun wantToSee(@Query("activity_id")id:Int):Single<ApiWrapper<ApiStatus>>
}