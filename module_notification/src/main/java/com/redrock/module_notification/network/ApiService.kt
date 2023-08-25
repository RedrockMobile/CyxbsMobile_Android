package com.redrock.module_notification.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.redrock.module_notification.bean.*
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

/**
 * Author by OkAndGreat
 * Date on 2022/5/1 10:49.
 *
 */
interface ApiService {
    /**
     * 获得所有信息
     * 包括活动信息和通知消息
     */
    @GET("/message-system/user/allMsg")
    fun getAllMsg(): Observable<RedrockApiWrapper<MsgBeanData>>

    /*
    获取活动消息(新版)
    * */
    @GET("/magipoke-ufield/message/list/")
    fun getUFieldActivity(): Observable<ApiWrapper<List<UfieldMsgBean>>>

    /*
    * 改变读取活动消息的状态
    * */
    @PUT("/magipoke-ufield/message/action/click/")
    fun changeUfieldMsgStatus(
        @Query("message_id") messageId: Int
    ): Observable<ApiWrapper<UfieldMsgStatusBean>>

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
}