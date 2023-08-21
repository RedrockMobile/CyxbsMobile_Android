package com.mredrock.cyxbs.ufield.lyt.network

import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.ufield.lyt.bean.TodoBean
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

/**
 *  author : lytMoon
 *  date : 2023/8/19 21:14
 *  description :负责待审核活动板块的apiService
 *  version ： 1.0
 */
interface TodoApiService {
    companion object {
        val INSTANCE by lazy { ApiGenerator.getApiService(TodoApiService::class) }
    }

    /**
     * 邮乐场获取待审核的活动的全部数据
     */
    @GET("/magipoke-ufield/activity/list/tobe-examine")
    fun getTodoList(): Single<ApiWrapper<List<TodoBean>>>


}