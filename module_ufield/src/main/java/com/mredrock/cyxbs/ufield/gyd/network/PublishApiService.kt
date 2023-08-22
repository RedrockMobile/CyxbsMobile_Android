package com.mredrock.cyxbs.ufield.gyd.network


import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.ufield.gyd.bean.StatusBean
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * description ： TODO:类的作用
 * author : 苟云东
 * email : 2191288460@qq.com
 * date : 2023/8/21 16:52
 */
interface PublishApiService {
    companion object {
        val INSTANCE by lazy { ApiGenerator.getApiService(PublishApiService::class) }
    }

    //发布活动的网络请求

    @Multipart
    @POST("/magipoke-ufield/activity/publish")
    fun getPublishStatus(
        @Part("activity_title") activityTitle: RequestBody,
        @Part("activity_type") activityType: RequestBody,
        @Part("activity_start_at") activityStartAt: RequestBody,
        @Part("activity_end_at") activityEndAt: RequestBody,
        @Part("activity_place") activityPlace: RequestBody,
        @Part("activity_registration_type") activityRegistrationType: RequestBody,
        @Part("activity_organizer") activityOrganizer: RequestBody,
        @Part("creator_phone") creatorPhone: RequestBody,
        @Part("activity_detail") activityDetail: RequestBody,
        @Part coverFile: MultipartBody.Part
    ): Single<ApiStatus>
}