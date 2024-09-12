package com.mredrock.cyxbs.ufield.network

import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.lib.utils.network.IApi
import com.mredrock.cyxbs.ufield.bean.ActivityBean
import com.mredrock.cyxbs.ufield.bean.SyncTime
import com.mredrock.cyxbs.ufield.bean.TodoListPushWrapper
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

/**
 *
 * author : 苟云东
 * email : 2191288460@qq.com
 * date : 2023/8/24 11:19
 */
interface ActivityDetailApiService : IApi {

    @GET("/magipoke-ufield/activity/")
    fun getActivityData(@Query("activity_id") id: Int): Single<ApiWrapper<ActivityBean>>

    @PUT("/magipoke-ufield/activity/action/watch/")
    fun wantToSee(@Query("activity_id") id: Int): Single<ApiWrapper<ApiStatus>>

    @POST("/magipoke-todo/batch-create")
    fun addTodo(@Body pushWrapper: TodoListPushWrapper):
            Single<ApiWrapper<SyncTime>>

    @PUT("/magipoke-ufield/activity/addTodo/")
    fun isAdd(@Query("activity_id") id: Int): Single<ApiWrapper<ApiStatus>>
}