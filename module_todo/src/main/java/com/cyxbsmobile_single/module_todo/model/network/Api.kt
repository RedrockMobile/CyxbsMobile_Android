package com.cyxbsmobile_single.module_todo.model.network

import com.cyxbsmobile_single.module_todo.model.bean.*
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Author: RayleighZ
 * Time: 2021-08-24 11:33
 */
interface Api {

    /**
     * 从数据库获取全部todo
     */
    @GET("/magipoke-todo/list")
    fun queryAllTodo():
            Observable<RedrockApiWrapper<TodoListSyncTimeWrapper>>

    /**
     * 获取自上次同步到现在之间修改的所有todo
     */
    @GET("/magipoke-todo/todos")
    fun queryChangedTodo(
        @Query("sync_time")
        syncTime: Long
    ): Observable<RedrockApiWrapper<TodoListGetWrapper>>

    /**
     * 上传todo到数据库
     */
    @POST("/magipoke-todo/batch-create")
    fun pushTodo(@Body pushWrapper: TodoListPushWrapper):
            Observable<RedrockApiWrapper<SyncTime>>

    /**
     * 获取最后修改的时间戳
     */
    @GET("/magipoke-todo/sync-time")
    fun getLastSyncTime():
            Observable<RedrockApiWrapper<SyncTime>>

    /**
     * 删除todo
     */
    @DELETE("/magipoke-todo/todos")
    fun delTodo(
        @Body delPushWrapper: DelPushWrapper
    ): Observable<RedrockApiWrapper<SyncTime>>
}