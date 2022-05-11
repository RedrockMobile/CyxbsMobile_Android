package com.redrock.module_notification.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.redrock.module_notification.bean.ActiveMsgBean
import com.redrock.module_notification.bean.ChangeReadStatusToBean
import com.redrock.module_notification.bean.DeleteMsgToBean
import com.redrock.module_notification.bean.SystemMsgBean
import com.redrock.module_notification.network.ApiService
import com.redrock.module_notification.util.Constant.NOTIFICATION_LOG_TAG

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:08.
 *
 */
class NotificationViewModel : BaseViewModel() {
    val activeMsg = MutableLiveData<List<ActiveMsgBean>>()
    val systemMsg = MutableLiveData<List<SystemMsgBean>>()
    val checkInStatus = MutableLiveData<Boolean>()

    //通知tablayout系统通知小红点显示状态
    val sysDotStatus = MutableLiveData<Boolean>()

    //通知tablayout活动通知小红点显示状态
    val activeDotStatus = MutableLiveData<Boolean>()

    //通知popupwindow是否可以点击
    val popupWindowClickableStatus = MutableLiveData<Boolean>()

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
                    Log.w(NOTIFICATION_LOG_TAG, "getAllMsg failed")
                },
                onNext = {
                    Log.d(NOTIFICATION_LOG_TAG, "getAllMsg: $it")
                    activeMsg.value = it.active_msg
                    systemMsg.value = it.system_msg
                }
            )
            .lifeCycle()
    }


    /**
     * 删除消息
     */
    fun deleteMsg(bean: DeleteMsgToBean) {
        retrofit.deleteMsg(bean)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy {
                toast("删除消息成功")
            }
            .lifeCycle()
    }

    /**
     * 改变消息已读状态
     */
    fun changeMsgStatus(bean: ChangeReadStatusToBean) {
        retrofit.changeMsgStatus(bean)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy {}
            .lifeCycle()
    }

    /**
     * 获取签到状态信息
     */
    fun getCheckInStatus() {
        ApiGenerator.getCommonApiService(ApiService::class.java)
            .getCheckInStatus()
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy {
                checkInStatus.value = it.isChecked
            }
            .lifeCycle()
    }

    /**
     * 通知改变tablayout系统通知小红点显示状态
     */
    fun changeSysDotStatus(status: Boolean) {
        sysDotStatus.value = status
    }

    /**
     * 通知改变tablayout活动通知小红点显示状态
     */
    fun changeActiveDotStatus(status: Boolean) {
        activeDotStatus.value = status
    }

    /**
     * 通知改变popupwindow是否可以弹出
     */
    fun changePopUpWindowClickableStatus(status: Boolean) {
        popupWindowClickableStatus.value = status
    }
}