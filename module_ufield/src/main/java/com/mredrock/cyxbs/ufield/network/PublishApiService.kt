package com.mredrock.cyxbs.ufield.network


import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

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
    fun postActivityWithCover(
        @PartMap activityDataMap: MutableMap<String, RequestBody>,
        @Part coverFile: MultipartBody.Part
    ): Single<ApiStatus>

    @Multipart
    @POST("/magipoke-ufield/activity/publish")
    fun postActivityNotCover(
        @PartMap activityDataMap: MutableMap<String, RequestBody>
    ): Single<ApiStatus>


}