package com.redrock.module_notification.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.redrock.module_notification.bean.ActiveMsgBean
import com.redrock.module_notification.bean.SystemMsgBean
import com.redrock.module_notification.network.ApiService
import com.redrock.module_notification.util.Constant.NOTIFICATION_LOG_TAG
import com.redrock.module_notification.util.Date
import com.redrock.module_notification.util.NotificationSp

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:08.
 *
 */
class NotificationViewModel : BaseViewModel() {
    val activeMsg = MutableLiveData<List<ActiveMsgBean>>()
    val systemMsg = MutableLiveData<List<SystemMsgBean>>()
    val hasUnread = MutableLiveData<Boolean>()

    private val retrofit by lazy { ApiGenerator.getApiService(ApiService::class.java) }

    /**
     * 获取所有通知信息
     */
    fun getAllMsg() {
        retrofit.getAllMsg()
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    Log.w(NOTIFICATION_LOG_TAG, "getAllMsg failed ")
                },
                onNext = {
                    activeMsg.value = it.active_msg
                    systemMsg.value = it.system_msg
                }
            )
            .lifeCycle()
    }

    /**
     * 获取是否有未读信息
     */
    fun getHasUnread() {
        retrofit.getHashUnreadMsg()
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    Log.w(NOTIFICATION_LOG_TAG, "getHasUnread ")
                },
                onNext = {
                    hasUnread.value = it.has
                }
            )
            .lifeCycle()
    }


}