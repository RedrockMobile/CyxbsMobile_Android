package com.mredrock.cyxbs.main.network

import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.lib.utils.network.IApi
import com.mredrock.cyxbs.main.bean.ItineraryMsgBean
import com.mredrock.cyxbs.main.bean.UfieldMsgBean
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * ...
 * @author: Black-skyline (Hu Shujun)
 * @email: 2031649401@qq.com
 * @date: 2023/9/5
 * @Description: 获取消息中心的消息
 *
 */
interface NotificationApiService : IApi {
    // 获取notification模块中的活动通知消息
    @GET("/magipoke-ufield/message/list/")
    suspend fun getUFieldActivityList(): ApiWrapper<List<UfieldMsgBean>>

    // 获取notification模块中的发送的行程
    @GET("/magipoke-jwzx/itinerary/allMsg")
    suspend fun getSentItinerary(@Query("typ") type: String = "sent"): ApiWrapper<List<ItineraryMsgBean>>

    // 获取notification模块中的接收的行程
    @GET("/magipoke-jwzx/itinerary/allMsg")
    suspend fun getReceivedItinerary(@Query("typ") type: String = "received"): ApiWrapper<List<ItineraryMsgBean>>

}