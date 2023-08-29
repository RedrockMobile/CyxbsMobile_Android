package com.redrock.module_notification.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.redrock.module_notification.bean.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

/**
 * Author by OkAndGreat
 * Date on 2022/5/1 10:49.
 *
 */
interface ApiService {
    companion object {
        val INSTANCE by lazy {
            ApiGenerator.getApiService(ApiService::class)
        }
    }

    /**
     * 获得所有信息
     * 包括活动信息和通知消息
     */
    @GET("/message-system/user/allMsg")
    fun getAllMsg(): Observable<RedrockApiWrapper<MsgBeanData>>

    /**
     * 删除消息
     */
    @HTTP(method = "DELETE", path = "/message-system/user/msg", hasBody = true)
    fun deleteMsg(@Body deleteBody: DeleteMsgToBean):
            Observable<RedrockApiWrapper<DeleteMsgFromBean>>

    /**
     * 改变已读消息状态
     */
    @PUT("/message-system/user/msgHasRead")
    fun changeMsgStatus(@Body changeBody: ChangeReadStatusToBean):
            Observable<RedrockApiWrapper<ChangeReadStatusFromBean>>

    /**
     * 检查是否签到
     */
    @POST("/magipoke-intergral/QA/User/getScoreStatus")
    fun getCheckInStatus(): Observable<RedrockApiWrapper<ScoreStatus>>

    /**
     * 获取行程消息中的sent itinerary
     */
    @GET("/magipoke-jwzx/itinerary/allMsg")
    fun getSentItinerary(@Query("typ") type: String = "sent"): Single<ApiWrapper<List<SentItineraryMsg>>>

    /**
     * 获取行程消息中的received itinerary
     */
    @GET("/magipoke-jwzx/itinerary/allMsg")
    fun getReceivedItinerary(@Query("typ") type: String = "received"): Single<ApiWrapper<List<ReceivedItineraryMsg>>>

    /**
     * 取消itineraryId对应的行程的提醒
     */
    @PUT("/magipoke-jwzx/itinerary/cancel")
    fun cancelItineraryReminder(@Field("id") itineraryId: String): Single<ApiStatus>

    /**
     * 添加进日程
     *
     * @param time          提前提醒时间
     * @param title         事务标题
     * @param content       事务内容
     * @param dateJson      事务json
     * @return
     */
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
    ): Single<ApiStatus>
}