package com.mredrock.cyxbs.food.network

import com.mredrock.cyxbs.food.network.bean.FoodMainBean
import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

/**
 * Create by bangbangp on 2023/2/14 18:46
 * Email:1678921845@qq.com
 * Description:
 */
interface FoodApiService {
    companion object {
        /**
         * FoodApiService的实例
         */
        val INSTANCE by lazy {
            ApiGenerator.getApiService(FoodApiService::class)
        }
    }

    /**
     * 美食首页图片数据；就餐区域、就餐人数、餐饮特征数据
     */
    @GET("/magipoke/eatFirst")
    fun getFoodMain():Single<ApiWrapper<FoodMainBean>>


}