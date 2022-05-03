package com.redrock.module_notification.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.redrock.module_notification.bean.*
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT

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
    fun getHashUnreadMsg(): Observable<RedrockApiWrapper<UnreadData>>

    /**
     * 获得所有信息
     * 包括活动信息和通知消息
     */
    @GET("/message-system/user/allMsg")
    fun getAllMsg(): Observable<RedrockApiWrapper<MsgBeanData>>

    /**
     * 删除消息
     */
    @DELETE("/message-system/user/msg")
    fun deleteMsg(@Body deleteBody: DeleteMsgToBean):
            Observable<RedrockApiWrapper<DeleteMsgFromBean>>

    /**
     * 改变已读消息状态
     */
    @PUT("/message-system/user/msgHasRead")
    fun changeMsgStatus(@Body changeBody: ChangeReadStatusToBean):
            Observable<RedrockApiWrapper<ChangeReadStatusFromBean>>
}