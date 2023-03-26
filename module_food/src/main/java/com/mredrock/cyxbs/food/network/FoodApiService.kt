package com.mredrock.cyxbs.food.network

import com.mredrock.cyxbs.food.network.bean.*
import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

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
    @GET("/magipoke-delicacy/HomePage")
    fun getFoodMain():Single<ApiWrapper<FoodMainBean>>




    @POST("/magipoke-delicacy/food/result")
    @FormUrlEncoded
    fun postFoodResult(@Field("eat_area") eatArea:List<String>,
    @Field("eat_num") eatNum:String,@Field("eat_property") eatProperty:List<String>):Single<ApiWrapper<List<FoodResultBeanItem>>>

   /* *//**
     * 随机美食数据
     *//*
    @POST("/magipoke-delicacy/food/result")
    fun postFoodResult(@Body requestBody:RequestBody):Single<ApiWrapper<List<FoodResultBeanItem>>>*/

    /**
     *点赞
     */
    @POST("/magipoke-delicacy/food/praise")
    @FormUrlEncoded
    fun postFoodPraise(@Field("name") name:String):Single<ApiWrapper<FoodPraiseBean>>

    /**
     * 刷新餐饮特征数据
     */
    @POST("/magipoke-delicacy/food/refresh")
    @FormUrlEncoded
    fun postFoodRefresh(@Field("eat_area") eatArea:List<String>,@Field("eat_num") eatNum:String):Single<ApiWrapper<FoodRefreshBean>>
}