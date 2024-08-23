package com.cyxbsmobile_single.module_todo.model.network

import com.cyxbsmobile_single.module_todo.model.bean.DelPushWrapper
import com.cyxbsmobile_single.module_todo.model.bean.SyncTime
import com.cyxbsmobile_single.module_todo.model.bean.TodoListGetWrapper
import com.cyxbsmobile_single.module_todo.model.bean.TodoListPushWrapper
import com.cyxbsmobile_single.module_todo.model.bean.TodoListSyncTimeWrapper
import com.cyxbsmobile_single.module_todo.model.bean.TodoPinData
import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * description:
 * author: sanhuzhen
 * date: 2024/8/17 11:05
 */
interface TodoApiService {
    companion object {
        val INSTANCE by lazy { ApiGenerator.getApiService(TodoApiService::class) }
    }

    /**
     * 从数据库获取全部todo
     */
    @GET("/magipoke-todo/list")
    fun queryAllTodo():
            Single<ApiWrapper<TodoListSyncTimeWrapper>>

    /**
     * 获取自上次同步到现在之间修改的所有todo
     */
    @GET("/magipoke-todo/todos")
    fun queryChangedTodo(
        @Query("sync_time")
        syncTime: Long
    ): Single<ApiWrapper<TodoListGetWrapper>>

    /**
     * 上传todo到数据库
     */
    @POST("/magipoke-todo/batch-create")
    fun pushTodo(@Body pushWrapper: TodoListPushWrapper):
            Single<ApiWrapper<SyncTime>>

    /**
     * 获取最后修改的时间戳
     */
    @GET("/magipoke-todo/sync-time")
    fun getLastSyncTime(
        @Query("sync_time")
        sync_time: Long
    ): Single<ApiWrapper<SyncTime>>

    /**
     * 删除todo
     */
    @HTTP(method = "DELETE", path = "/magipoke-todo/todos", hasBody = true)
    fun delTodo(
        @Body delPushWrapper: DelPushWrapper
    ): Single<ApiWrapper<SyncTime>>

    /**
     * 获取分组的数据
     */

    @POST("/magipoke-todo/type-list")
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun getTodoListByStudy(
        @Field("type")
        type: String = "study"
    ): Single<ApiWrapper<TodoListSyncTimeWrapper>>


    @POST("/magipoke-todo/type-list")
    @FormUrlEncoded
    fun getTodoListByLife(
        @Field("type")
        type: String = "life"
    ): Single<ApiWrapper<TodoListSyncTimeWrapper>>


    @POST("/magipoke-todo/type-list")
    @FormUrlEncoded
    fun getTodoListByOther(
        @Field("type")
        type: String = "other"
    ): Single<ApiWrapper<TodoListSyncTimeWrapper>>

    /**
     * 置顶
     */
    @POST("/magipoke-todo/pin")
    fun pinTodo(
        @Body todoPinData: TodoPinData
    ): Single<ApiWrapper<SyncTime>>
}