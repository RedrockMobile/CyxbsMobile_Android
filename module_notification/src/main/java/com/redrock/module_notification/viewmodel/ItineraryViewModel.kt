package com.redrock.module_notification.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.lib.utils.network.throwOrInterceptException
import com.mredrock.cyxbs.lib.utils.utils.LogUtils
import com.redrock.module_notification.bean.ReceivedItineraryMsgBean
import com.redrock.module_notification.bean.SentItineraryMsgBean
import com.redrock.module_notification.model.NotificationRepository

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
    val sentItineraryList: LiveData<List<SentItineraryMsgBean>> get() = _sentItineraryList
    private val _sentItineraryList = MutableLiveData<List<SentItineraryMsgBean>>()

    val allUnReadSentItineraryIds: LiveData<List<Int>> get() = _allUnReadSentItineraryIds
    private val _allUnReadSentItineraryIds = MutableLiveData<List<Int>>()
    val newUnReadSentItineraryIds = _allUnReadSentItineraryIds.asShareFlow()

    // 获取-已经发送的行程消息列表 请求是否成功（状态）
    val sentItineraryListIsSuccessfulState: LiveData<Boolean> get() = _sentItineraryListIsSuccessfulState
    private val _sentItineraryListIsSuccessfulState = MutableLiveData<Boolean>()

    // 获取-已经发送的行程消息列表 请求是否成功（事件）
    val sentItineraryListIsSuccessfulEvent = _sentItineraryListIsSuccessfulState.asShareFlow()



    // 用户被通知到的行程消息的列表
    val receivedItineraryList: LiveData<List<ReceivedItineraryMsgBean>> get() = _receivedItineraryList
    private val _receivedItineraryList = MutableLiveData<List<ReceivedItineraryMsgBean>>()

    val allUnReadReceivedItineraryIds: LiveData<List<Int>> get() = _allUnReadReceivedItineraryIds
    private val _allUnReadReceivedItineraryIds = MutableLiveData<List<Int>>()
    val newUnReadReceivedItineraryIds = _allUnReadReceivedItineraryIds.asShareFlow()


    // 获取-被通知到的行程消息列表 请求是否成功（状态）
    val receivedItineraryListIsSuccessfulState: LiveData<Boolean> get() = _receivedItineraryListIsSuccessfulState
    private val _receivedItineraryListIsSuccessfulState = MutableLiveData<Boolean>()

    // 获取-被通知到的行程消息列表 请求是否成功（事件）
    val receivedItineraryListIsSuccessfulEvent =
        _receivedItineraryListIsSuccessfulState.asShareFlow()



    // 取消某行程的提醒 请求是否成功（状态）
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
                    _sentItineraryListIsSuccessfulState.postValue(true)
                    _sentItineraryList.postValue(it)
                }
        } else {
            if (hostViewModel.getItineraryIsSuccessful) {
                LogUtils.d("Hsj-getSentItinerary","从宿主Activity的viewModel中获取SentItinerary")
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

                    _receivedItineraryListIsSuccessfulState.postValue(true)
                    _receivedItineraryList.postValue(it)
                }
        } else {
            if (hostViewModel.getItineraryIsSuccessful) {
                LogUtils.d("Hsj-getReceivedItinerary","从宿主Activity的viewModel中获取ReceivedItinerary")
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
            .safeSubscribeBy (
                onError = {
                    it.printStackTrace()
                    LogUtils.d("Hsj-cancelItineraryReminder","err: ${it.printStackTrace()}")
                          },
                onSuccess = {
                _cancelReminderIsSuccessfulState.postValue(Pair(index, true))
            })
    }

    /**
     * 改变行程已读状态
     * @param unReadIdList
     * @param status
     */
    fun changeItineraryReadStatus(unReadIdList: List<Int>,page: Int, status: Boolean = true) {
        if (unReadIdList.isEmpty()) return
        NotificationRepository.changeItineraryReadStatus(unReadIdList, status)
            .throwOrInterceptException {
                ApiException{exception ->
                    LogUtils.w("Hsj-changeItineraryReadStatus","changeReadStatus Exception status:${exception.status}")
                }
                LogUtils.w("Hsj-changeItineraryReadStatus","changeReadStatus Exception:${it.message}")
                LogUtils.w("Hsj-changeItineraryReadStatus","changeReadStatus fail")
            }
            .safeSubscribeBy (
                onError = {
                    it.printStackTrace()
                    LogUtils.d("Hsj-changeItineraryReadStatus","err: ${it.printStackTrace()}")
                },
                onSuccess = {
                LogUtils.d("Hsj-changeItineraryReadStatus","changeReadStatus success")
                when(page) {
                    0 -> _allUnReadReceivedItineraryIds.value = emptyList()
                    1 -> _allUnReadSentItineraryIds.value = emptyList()
                    else ->{}
                }
            })
    }


    /**
     * 改变行程是否添加到了日程的状态
     * @param itineraryId
     * @param status
     */
    private fun changeItineraryAddStatus(itineraryId: Int, status: Boolean = true) {
        NotificationRepository.changeItineraryAddStatus(itineraryId, status)
            .throwOrInterceptException {
                "与服务器君通信失败 TAT~".toast()
                ApiException{exception ->
                    LogUtils.w("Hsj-Itinerary","changeAddStatus Exception status:${exception.status}")
                }
                LogUtils.w("Hsj-Itinerary","changeAddStatus Exception:${it.message}")
            }
            .safeSubscribeBy(
                onError = {
                    it.printStackTrace()
                    LogUtils.d("Hsj-changeItineraryAddStatus","err: ${it.printStackTrace()}")
                },
                onSuccess = {
                    LogUtils.d("Hsj-changeItineraryAddStatus","changeAddStatus Success")
                }
            )
    }

    /**
     * 把行程添加到日程(课表事务)
     * @param index
     * @param remindTime
     * @param info
     */
    fun addItineraryToSchedule(
        index: Int,
        remindTime: Int,
        info: ReceivedItineraryMsgBean
    ) {
        NotificationRepository.addAffair(remindTime, info)
            .throwOrInterceptException {
//                "添加失败".toast()
//                ApiException{exception->
//                    LogUtils.w("Hsj-Itinerary","add2Schedule Exception status:${exception.status}")
//                }
//                LogUtils.w("Hsj-Itinerary","add2Schedule Exception:${it.message}")
//                _add2scheduleIsSuccessfulState.postValue(Pair(index, false))
            }
            .safeSubscribeBy (
                onError = {
                    it.printStackTrace()
                    LogUtils.d("Hsj-addItineraryToSchedule","err: ${it.printStackTrace()}")
                },
                onSuccess = {
                "添加成功".toast()
                changeItineraryAddStatus(info.id)
                _add2scheduleIsSuccessfulState.postValue(Pair(index, true))
            })
    }

    fun changeCurrentPageIndex(index: Int) {
        _currentPageIndex.value = index
    }

    fun setUnReadSentItineraryIds(list: List<Int>) {
        _allUnReadSentItineraryIds.value = list
    }

    fun setUnReadReceivedItineraryIds(list: List<Int>) {
        _allUnReadReceivedItineraryIds.value = list
    }

    init {
//        getReceivedItinerary()
//        getSentItinerary()
    }
}