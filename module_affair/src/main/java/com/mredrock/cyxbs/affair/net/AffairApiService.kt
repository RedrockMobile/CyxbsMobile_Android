package com.mredrock.cyxbs.affair.net

import com.mredrock.cyxbs.affair.bean.AddAffairBean
import com.mredrock.cyxbs.affair.bean.AffairBean
import com.mredrock.cyxbs.api.affair.NotificationBean
import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/3 12:08
 */
interface AffairApiService {

  @POST("/magipoke-reminder/Person/addTransaction")
  @FormUrlEncoded
  @Headers("App-Version:74")
  fun addAffair(
    @Field("time")
    time: Int,
    @Field("title")
    title: String,
    @Field("content")
    content: String,
    @Field("date")
    dateJson: String // 为 json 序列化后的 string
  ): Single<AddAffairBean>

  @POST("/magipoke-reminder/Person/getTransaction")
  @Headers("App-Version:74")
  fun getAffair(): Single<AffairBean>

  @POST("/magipoke-reminder/Person/editTransaction")
  @FormUrlEncoded
  @Headers("App-Version:74")
  fun updateAffair(
    @Field("id")
    remoteId: Int,
    @Field("time")
    time: Int,
    @Field("title")
    title: String,
    @Field("content")
    content: String,
    @Field("date")
    dateJson: String
  ): Single<ApiStatus>

  @POST("/magipoke-reminder/Person/deleteTransaction")
  @FormUrlEncoded
  @Headers("App-Version:74")
  fun deleteAffair(
    @Field("id")
    remoteId: Int
  ): Single<ApiStatus>

  @GET("/magipoke-reminder/Person/getHotWord")
  fun getTitleCandidate(): Single<ApiWrapper<List<String>>>

  @GET("/magipoke-jwzx/itinerary/hotLocation")
  fun getHotLocation(): Single<ApiWrapper<List<String>>>

  // 没课约专属发送通知接口
  @POST("magipoke-jwzx/itinerary")
  @Headers("App-Version:74")
  fun sendNotification(@Body notification : NotificationBean) : Single<ApiStatus>

  companion object {
    val INSTANCE by lazy {
      ApiGenerator.getApiService(AffairApiService::class)
    }
  }
}