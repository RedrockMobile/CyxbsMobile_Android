package com.redrock.module_notification.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.lib.utils.network.throwOrInterceptException
import com.redrock.module_notification.bean.ReceivedItineraryMsg
import com.redrock.module_notification.bean.SentItineraryMsg
import com.redrock.module_notification.model.NotificationRepository
import com.redrock.module_notification.util.Constant
import com.redrock.module_notification.util.NotificationSp

/**
 * ...
 * @author: Black-skyline (Hu Shujun)
 * @email: 2031649401@qq.com
 * @date: 2023/8/22
 * @Description:
 *
 */
class ItineraryViewModel(val hostViewModel: NotificationViewModel) : BaseViewModel() {
    // 用户已经发送的行程消息的列表
    val sentItineraryList: LiveData<List<SentItineraryMsg>> get() = _sentItineraryList
    private val _sentItineraryList = MutableLiveData<List<SentItineraryMsg>>()

    // 获取-已经发送的行程消息列表 请求是否成功（状态）
    val sentItineraryListIsSuccessfulState: LiveData<Boolean> get() = _sentItineraryListIsSuccessfulState
    private val _sentItineraryListIsSuccessfulState = MutableLiveData<Boolean>()

    // 获取-已经发送的行程消息列表 请求是否成功（事件）
    val sentItineraryListIsSuccessfulEvent = _sentItineraryListIsSuccessfulState.asShareFlow()

    // 上一次已发送的行程数据更新时间
    var lastSentItineraryUpdateTime =
        appContext.NotificationSp.getLong(Constant.LAST_SENT_ITINERARY_PAGE_READ_TIME, 0L)
        private set


    // 用户被通知到的行程消息的列表
    val receivedItineraryList: LiveData<List<ReceivedItineraryMsg>> get() = _receivedItineraryList
    private val _receivedItineraryList = MutableLiveData<List<ReceivedItineraryMsg>>()

    // 获取-被通知到的行程消息列表 请求是否成功（状态）
    val receivedItineraryListIsSuccessfulState: LiveData<Boolean> get() = _receivedItineraryListIsSuccessfulState
    private val _receivedItineraryListIsSuccessfulState = MutableLiveData<Boolean>()

    // 获取-被通知到的行程消息列表 请求是否成功（事件）
    val receivedItineraryListIsSuccessfulEvent =
        _receivedItineraryListIsSuccessfulState.asShareFlow()

    // 上一次被通知的行程数据更新时间
    var lastReceivedItineraryUpdateTime =
        appContext.NotificationSp.getLong(Constant.LAST_RECEIVED_ITINERARY_PAGE_READ_TIME, 0L)
        private set


    // 取消某行程的提醒 请求是否成功（状态）
    val cancelReminderIsSuccessfulState: LiveData<Pair<Int, Boolean>> get() = _cancelReminderIsSuccessfulState
    private val _cancelReminderIsSuccessfulState = MutableLiveData<Pair<Int, Boolean>>()

    // 取消某行程的提醒 请求是否成功（事件）
    val cancelReminderIsSuccessfulEvent = _cancelReminderIsSuccessfulState.asShareFlow()


    // 添加某一行程到日程（添加事务） 请求是否成功（状态）
    val add2scheduleIsSuccessfulState: LiveData<Pair<Int, Boolean>> get() = _add2scheduleIsSuccessfulState
    private val _add2scheduleIsSuccessfulState = MutableLiveData<Pair<Int, Boolean>>()

    // 添加某一行程到日程（添加事务） 请求是否成功（事件）
    val add2scheduleIsSuccessfulEvent = _add2scheduleIsSuccessfulState.asShareFlow()

    // 当前行程fragment展示的哪个页面，0为接收fragment，1为发送fragment
    val currentPageIndex: LiveData<Int> get() = _currentPageIndex
    private val _currentPageIndex = MutableLiveData(0)

    /**
     * 通过网络请求获取 已经发送的行程列表
     */
    fun getSentItinerary(isForcedRefresh: Boolean = false) {
        if (isForcedRefresh) {
            NotificationRepository.getSentItinerary()
                .mapOrInterceptException {
                    _sentItineraryListIsSuccessfulState.postValue(false)
                }
                .safeSubscribeBy {
                    lastSentItineraryUpdateTime = System.currentTimeMillis()
                    _sentItineraryListIsSuccessfulState.postValue(true)
                    _sentItineraryList.postValue(it)
                }
        } else {
            if (hostViewModel.getItineraryIsSuccessful) {
                lastSentItineraryUpdateTime = hostViewModel.itineraryUpdateTime
                _sentItineraryListIsSuccessfulState.postValue(true)
                _sentItineraryList.postValue(hostViewModel.itineraryMsg.value!!.sentItineraryList)
            } else {
                _sentItineraryListIsSuccessfulState.postValue(false)
            }
        }
    }

    /**
     * 通过网络请求获取 被通知到的行程列表
     */
    fun getReceivedItinerary(isForcedRefresh: Boolean = false) {
        if (isForcedRefresh) {
            NotificationRepository.getReceivedItinerary()
                .mapOrInterceptException {
                    _receivedItineraryListIsSuccessfulState.postValue(false)
                }
                .safeSubscribeBy {
                    lastReceivedItineraryUpdateTime = System.currentTimeMillis()
                    _receivedItineraryListIsSuccessfulState.postValue(true)
                    _receivedItineraryList.postValue(it)
                }
        } else {
            if (hostViewModel.getItineraryIsSuccessful) {
                lastReceivedItineraryUpdateTime = hostViewModel.itineraryUpdateTime
                _receivedItineraryListIsSuccessfulState.postValue(true)
                _receivedItineraryList.postValue(hostViewModel.itineraryMsg.value!!.receivedItineraryList)
            } else {
                _receivedItineraryListIsSuccessfulState.postValue(false)
            }
        }
    }

    /**
     * 传入行程id和该行程在rv数据中的索引，取消该行程的提醒
     */
    fun cancelItineraryReminder(itineraryId: Int, index: Int) {
        NotificationRepository.cancelItineraryReminder(itineraryId)
            .throwOrInterceptException {
                "取消失败".toast()
                _cancelReminderIsSuccessfulState.postValue(Pair(index, false))
            }
            .safeSubscribeBy {
                _cancelReminderIsSuccessfulState.postValue(Pair(index, true))
            }
    }

    fun addItineraryToSchedule(
        index: Int,
        time: Int,
        title: String,
        content: String,
        dateJson: String
    ) {
        NotificationRepository.addAffair(time, title, content, dateJson)
            .throwOrInterceptException {
                "添加失败".toast()
                _add2scheduleIsSuccessfulState.postValue(Pair(index, false))
            }
            .safeSubscribeBy {
                "添加成功".toast()
                _add2scheduleIsSuccessfulState.postValue(Pair(index, true))
            }
    }

    fun changeCurrentPageIndex(index: Int) {
        _currentPageIndex.value = index
    }

    init {
        getReceivedItinerary()
        getSentItinerary()
    }
}