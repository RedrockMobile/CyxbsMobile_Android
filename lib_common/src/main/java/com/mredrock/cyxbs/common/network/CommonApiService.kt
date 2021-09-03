package com.mredrock.cyxbs.common.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.utils.down.bean.DownMessage
import com.mredrock.cyxbs.common.utils.down.params.DownMessageParams
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by yyfbe, Date on 2020/4/7.
 */
interface CommonApiService {
    /**
     * 下发接口，目前适用于title--content，公共可用
     * exp:
     *  ApiGenerator
     * .getCommonApiService(CommonApiService::class.java)
     * .getDownMessage(DownMessageParams("name"))
     */
    @Headers("Content-Type: application/json")
    @POST("/magipoke-text/text/get")
    fun getDownMessage(@Body downMessageParams: DownMessageParams): Observable<RedrockApiWrapper<DownMessage>>

    // 用于改变积分商城界面的任务
    @POST("/magipoke-intergral/Integral/progress")
    @FormUrlEncoded
    fun postTaskIsSuccessful(
        @Field("title") title: String,
        @Field("current_progress") currentProgress: Int
    ): Observable<RedrockApiStatus>
}