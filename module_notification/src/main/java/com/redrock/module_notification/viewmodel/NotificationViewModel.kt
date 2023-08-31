package com.redrock.module_notification.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.setSchedulers
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.network.mapOrThrowApiException
import com.redrock.module_notification.bean.ChangeReadStatusToBean
import com.redrock.module_notification.bean.DeleteMsgToBean
import com.redrock.module_notification.bean.ItineraryAllMsg
import com.redrock.module_notification.bean.SystemMsgBean
import com.redrock.module_notification.bean.UfieldMsgBean
import com.redrock.module_notification.network.ApiService
import com.redrock.module_notification.util.Constant.NOTIFICATION_LOG_TAG
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:08.
 *
 */
class NotificationViewModel : BaseViewModel() {

    private val _ufieldActivityMsg = MutableLiveData<List<UfieldMsgBean>>()

    val ufieldActivityMsg: LiveData<List<UfieldMsgBean>> get() = _ufieldActivityMsg

    // val activeMsg = MutableLiveData<List<ActiveMsgBean>>()
    val systemMsg = MutableLiveData<List<SystemMsgBean>>()

    // 所有可收到的行程消息的列表
    val itineraryMsg: LiveData<ItineraryAllMsg> get() = _itineraryMsg
    private val _itineraryMsg = MutableLiveData<ItineraryAllMsg>()

    // 在该viewModel中获取所有行程消息的请求 是否成功（状态）
    val itineraryMsgIsSuccessfulState: LiveData<Boolean> get() = _itineraryMsgIsSuccessfulState
    private val _itineraryMsgIsSuccessfulState = MutableLiveData<Boolean>()
    var getItineraryIsSuccessful: Boolean  = false
        private set
    var itineraryUpdateTime: Long = 0L
        private set

    val checkInStatus = MutableLiveData<Boolean>()

    //通知tablayout行程通知小红点显示状态
    val itineraryDotStatus: LiveData<Boolean> get() = _itineraryDotStatus
    private val _itineraryDotStatus = MutableLiveData<Boolean>()

    //通知tablayout系统通知小红点显示状态
    val sysDotStatus = MutableLiveData<Boolean>()

    //通知tablayout活动通知小红点显示状态
    val activeDotStatus = MutableLiveData<Boolean>()

    //通知popupwindow是否可以点击 当处于多选删除时不可以出现popupwindow
    val popupWindowClickableStatus = MutableLiveData<Boolean>()

    //改变消息已读状态的Status
    val changeMsgReadStatus = MutableLiveData<Int>()

    //获取数据是否成功
    val getMsgSuccessful = MutableLiveData<Boolean>()

    //获取游乐场的消息是否成功
    val getUfieldMsgSuccessful = MutableLiveData<Boolean>()

    private val retrofit by lazy { ApiGenerator.getApiService(ApiService::class.java) }

    /*
    * 获取活动消息
    * */
    fun getUFieldActivity() {
        retrofit.getUFieldActivity()
            .setSchedulers()
            .mapOrThrowApiException()
            .unsafeSubscribeBy(
                onError = {
                    toast("服务君似乎打盹了呢~")
                    getUfieldMsgSuccessful.value = false
                },
                onNext = {
                    Log.d("hui", "getUFieldActivity: $it")
                    _ufieldActivityMsg.postValue(it)
                    getUfieldMsgSuccessful.value = true
                }
            ).lifeCycle()

    }
    /*
    * 改变活动消息的读取状态
    * */

    fun changeUfieldMsgStatus(messageId: Int) {
        retrofit.changeUfieldMsgStatus(messageId)
            .setSchedulers()
            .mapOrThrowApiException()
            .unsafeSubscribeBy(
                onError = {
                    Log.d("hui", "changeUfieldMsgStatus1:$it ")
                },
                onNext = {
                    Log.d("hui", "changeUfieldMsgStatus2: $it")
                }
            ).lifeCycle()
    }

    /**
     * 获取所有活动和系统通知信息
     */
    fun getAllMsg() {
        retrofit.getAllMsg()
            .setSchedulers()
            .mapOrThrowApiException()
            .unsafeSubscribeBy(
                onError = {
                    /**
                     * onNext第一次发送null的时候会到onError，第二次就不会了
                     */
                    if (it is NullPointerException) {
                        getAllMsg()
                    } else {
                        Log.w(NOTIFICATION_LOG_TAG, "getAllMsg failed $it")
                        getMsgSuccessful.value = false
                    }
                },
                onNext = {
                    Log.d(NOTIFICATION_LOG_TAG, "getAllMsg: $it")
                    //  activeMsg.value = it.active_msg
                    systemMsg.value = it.system_msg
                    getMsgSuccessful.value = true
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
            .unsafeSubscribeBy {
                toast("删除消息成功")
                Log.d("wzt", "deleteMsg: ")

            }
            .lifeCycle()
    }

    /**
     * 改变消息已读状态
     * 我们约定position >= 0 为系统通知的消息 <=0 为活动通知的消息
     * 如果是null则是改变所有消息的可读状态
     */
    fun changeMsgStatus(bean: ChangeReadStatusToBean, position: Int? = null) {
        retrofit.changeMsgStatus(bean)
            .mapOrThrowApiException()
            .setSchedulers()
            .unsafeSubscribeBy {}
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
            .unsafeSubscribeBy {
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
     * 通知改变tablayout行程通知小红点显示状态
     */
    fun changeItineraryDotStatus(status: Boolean) {
        _itineraryDotStatus.value = status
    }

    /**
     * 通知改变popupwindow是否可以弹出
     */
    fun changePopUpWindowClickableStatus(status: Boolean) {
        popupWindowClickableStatus.value = status
    }

    /**
     * 获取所有行程消息
     */
    fun getAllItineraryMsg() {
        retrofit.getReceivedItinerary()
            .flatMap {received->
                retrofit.getSentItinerary().map {sent->
                    val emptyItineraryMsg = ItineraryAllMsg(sent.data, received.data)
                    emptyItineraryMsg
                }.subscribeOn(Schedulers.io())
            }
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .unsafeSubscribeBy (
                onError = {
                    Log.w(NOTIFICATION_LOG_TAG, "getAllItineraryMsg failed of ${it.message}")
                    _itineraryMsgIsSuccessfulState.postValue(false)
                    getItineraryIsSuccessful = false
                },
                onSuccess = {
                    itineraryUpdateTime = System.currentTimeMillis()
                    _itineraryMsg.value = it
                    getItineraryIsSuccessful = true
                    _itineraryMsgIsSuccessfulState.postValue(true)
                }
            ).lifeCycle()
    }
}