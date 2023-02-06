package com.mredrock.cyxbs.store.network

import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.lib.utils.network.IApi
import com.mredrock.cyxbs.store.bean.*
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

/**
 *    author : zz、985892345
 *    e-mail : 1140143252@qq.com、2767465918@qq.com
 *    date   : 2021/8/12 8:59
 */
interface ApiService : IApi {

    // 获取邮票中心界面
    @GET("/magipoke-intergral/User/info")
    fun getStampCenter(): Single<ApiWrapper<StampCenter>>

    //获取兑换详细界面内容
    @GET("/magipoke-intergral/Integral/getItemInfo")
    fun getProductDetail(
        @Query("id")
        id: Int
    ): Single<ApiWrapper<ProductDetail>>

    //购买商品
    @POST("/magipoke-intergral/Integral/purchase")
    @FormUrlEncoded
    fun buyProduct(
        @Field("id")
        id: Int
    ): Single<ApiWrapper<ExchangeState>>

    //得到兑换记录
    @GET("/magipoke-intergral/User/exchange")
    fun getExchangeRecord(
    ): Single<ApiWrapper<List<ExchangeRecord>>>

    //得到邮票获取记录
    @GET("/magipoke-intergral/User/getRecord")
    fun getStampGetRecord(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Single<ApiWrapper<List<StampGetRecord>>>
}