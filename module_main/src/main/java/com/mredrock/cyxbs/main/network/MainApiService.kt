package com.mredrock.cyxbs.main.network

import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.lib.utils.network.IApi
import com.mredrock.cyxbs.main.bean.StartPageBean
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

/**
 * Created By jay68 on 2018/8/10.
 */
interface MainApiService : IApi {
  @GET("/magipoke-text/start/photo")
  fun getStartPage(): Single<ApiWrapper<List<StartPageBean>>>
}