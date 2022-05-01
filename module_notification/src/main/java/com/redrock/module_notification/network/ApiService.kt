package com.redrock.module_notification.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.redrock.module_notification.bean.MsgBeanData
import com.redrock.module_notification.bean.UnreadData
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

/**
 * Author by OkAndGreat
 * Date on 2022/5/1 10:49.
 *
 */
interface ApiService {
    /**
     * 是否有未读信息
     */
    @GET("/message-system/user/msgHasRead")
    fun getHashUnreadMsg() : Observable<RedrockApiWrapper<UnreadData>>

    /**
     * 获得所有信息
     * 包括活动信息和通知消息
     */
    @GET("/message-system/user/allMsg")
    fun getAllMsg() : Observable<RedrockApiWrapper<MsgBeanData>>
}