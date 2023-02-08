package com.mredrock.cyxbs.declare.pages.main.net

import com.mredrock.cyxbs.declare.pages.main.DeclareDetailBean
import com.mredrock.cyxbs.declare.pages.main.HomeDataBean
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.lib.utils.network.IApi
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/4
 * @Description:
 */
interface ApiService : IApi {
    @GET("/magipoke-attitude/declare/homepage")
    fun getHomeData(): Single<ApiWrapper<List<HomeDataBean>>>

    @GET("/magipoke-attitude/declare")
    fun getDetailDeclareData(@Query("id") id: Int): Single<ApiWrapper<DeclareDetailBean>>
}