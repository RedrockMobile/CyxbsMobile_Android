package com.mredrock.cyxbs.ufield.network

import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.ufield.bean.DoneBean
import com.mredrock.cyxbs.ufield.bean.TodoBean
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

/**
 *  author : lytMoon
 *  date : 2023/8/19 21:14
 *  description :负责待审核活动板块的apiService
 *  version ： 1.0
 */
interface CheckApiService {
    companion object {
        val INSTANCE by lazy { ApiGenerator.getApiService(CheckApiService::class) }
    }

    @GET("/magipoke-ufield/activity/list/tobe-examine")
    fun getTodoList(): Single<ApiWrapper<List<TodoBean>>>

    @GET("/magipoke-ufield/activity/list/tobe-examine")
    fun getTodoUpList(@Query("lower_id")lower_id:Int): Single<ApiWrapper<List<TodoBean>>>


    @PUT("/magipoke-ufield/activity/action/examine/")
    fun passDecision(
        @Query("activity_id") id: Int,
        @Query("decision") decision: String = "pass"
    ): Single<ApiStatus>


    @PUT("/magipoke-ufield/activity/action/examine/")
    fun rejectDecision(
        @Query("activity_id") id: Int,
        @Query("reject_reason") reason: String,
        @Query("decision") decision: String = "reject"
    ): Single<ApiStatus>

    @GET("/magipoke-ufield/activity/list/examined/")
    fun getDoneList(): Single<ApiWrapper<List<DoneBean>>>


    @GET("/magipoke-ufield/activity/list/examined/")
    fun getDoneUpList(@Query("upper_id") upperID: Int): Single<ApiWrapper<List<DoneBean>>>

}