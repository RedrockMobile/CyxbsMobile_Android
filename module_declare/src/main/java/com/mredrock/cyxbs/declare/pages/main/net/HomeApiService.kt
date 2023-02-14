package com.mredrock.cyxbs.declare.pages.main.net

import com.mredrock.cyxbs.declare.pages.main.bean.HomeDataBean
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.lib.utils.network.IApi
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

/**
 * ... 主页网络请求Api
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/4
 * @Description:
 */
interface HomeApiService : IApi {
    /**
     * 获取主页数据
     */
    @GET("/magipoke-attitude/declare/homepage")
    fun getHomeData(): Single<ApiWrapper<List<HomeDataBean>>>
}