package com.mredrock.cyxbs.sport.model.network

import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.sport.model.SportDetailBean
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

/**
 * @author : why
 * @time   : 2022/8/5 21:15
 * @bless  : God bless my code
 */
interface SportDetailApiService {
    companion object {
        /**
         * SportDetailApiService的实例
         */
        val INSTANCE by lazy {
            // 因为体育打卡接口是现扒的，所以需要额外设置超时时间
            ApiGenerator.createSelfRetrofit(true) {
                OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
            }.create(SportDetailApiService::class.java)
        }
    }

    /**
     * 获取体育打卡详情页面数据
     */
    @GET("/magipoke/sunSport")
    fun getSportDetailData(): Single<ApiWrapper<SportDetailBean>>
}