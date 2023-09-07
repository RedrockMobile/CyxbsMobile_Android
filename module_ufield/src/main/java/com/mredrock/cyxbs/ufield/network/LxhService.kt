package com.mredrock.cyxbs.ufield.network

import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.ufield.bean.MsgBeanData
import com.mredrock.cyxbs.ufield.bean.RankBean
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface LxhService {
    companion object {
        val INSTANCE by lazy {
            ApiGenerator.getApiService(LxhService::class)
        }
    }

    //获取排行榜里的数据
    @GET("/magipoke-ufield/activity/search")
    fun getRank(
        @Query("activity_type") type: String,
        @Query("activity_num") number: Int,
        @Query("order_by") order: String
    ): Observable<ApiWrapper<List<RankBean>>>

    @GET("/magipoke-ufield/activity/list/me/")
    fun getAllMsg(): Observable<ApiWrapper<MsgBeanData>>
}