package com.mredrock.cyxbs.ufield.lyt.network

import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.ufield.lyt.bean.AllActivityBean
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

/**
 *  description :
 *  author : lytMoon
 *  date : 2023/8/20 19:02
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */
interface UFieldActivityApiService {
    companion object {
        val INSTANCE by lazy { ApiGenerator.getApiService(UFieldActivityApiService::class) }
    }
    /**
     * 获得全部都数据
     */
    @GET("/magipoke-ufield/activity/list/all")
    fun getAllActivityData(): Single<ApiWrapper<AllActivityBean>>


}