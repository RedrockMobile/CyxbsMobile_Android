package com.mredrock.cyxbs.mine.page.ask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.mine.network.model.AskPosted
import com.mredrock.cyxbs.mine.network.model.Draft
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.normalWrapper

/**
 * Created by zia on 2018/9/10.
 */
class AskViewModel : BaseViewModel() {

    private val stuNum = ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()
    private val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")

    private var askDraftPage: Int = 1
    private var askPostedPage: Int = 1

    private val pageSize = 6

    //askposted部分
    private val _eventOnAskPosted = SingleLiveEvent<Boolean>()//true代表加载完，false代表加载错误
    val eventOnAskPosted: LiveData<Boolean>
        get() = _eventOnAskPosted

    private val _askPosted = MutableLiveData<MutableList<AskPosted>>()
    val askPosted: LiveData<List<AskPosted>>
        get() = Transformations.map(_askPosted) {
            it.toList()
        }

    //草稿部分
    val askDraftEvent = MutableLiveData<List<Draft>>()
    val deleteEvent = MutableLiveData<Draft>()

    fun loadAskPostedList() {
        apiService.getAskPostedList(stuNum, idNum
                ?: return, askPostedPage++, pageSize)
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            if (it.isEmpty()) {
                                _eventOnAskPosted.postValue(true)
                                return@safeSubscribeBy
                            }

                            val localAskPosted = _askPosted.value ?: mutableListOf()
                            localAskPosted.addAll(it)
                            _askPosted.postValue(localAskPosted)
                        },
                        onError = {
                            _eventOnAskPosted.postValue(false)
                        })
                .lifeCycle()
    }

    fun cleanAskPostedPage() {
        //清除还在请求网络的接口,
        //如果一直刷新，那么前一个网络请求没有cancel掉，那么就会导致多的item
        onCleared()

        askPostedPage = 1
        _askPosted.value = mutableListOf()
    }

//    fun loadAskDraftList() {
////        apiService.getDraftList(user?.stuNum ?: return, user?.idNum
////                ?: return, askDraftPage++, pageSize)
////                .mapOrThrowApiException()
////                .map { list ->
////                    list.forEach { it.parseQuestion() }
////                    list
////                }
////                .setSchedulers()
////                .doOnErrorWithDefaultErrorHandler { false }
////                .safeSubscribeBy(
////                        onNext = { it ->
////                            val askDraftList = it.filter {
////                                it.type == "question"
////                            }
////                            askDraftEvent.postValue(askDraftList)
////                        },
////                        onError = {
////                            it.printStackTrace()
//////                            errorEvent.postValue(it.message)
////                        }
////                )
////                .lifeCycle()
//    }
//
//    fun deleteDraft(draft: Draft) {
//        apiService.deleteDraft(user?.stuNum ?: return, user?.idNum ?: return, draft.id)
//                .checkError()
//                .setSchedulers()
//                .doOnErrorWithDefaultErrorHandler { false }
//                .safeSubscribeBy(
//                        onNext = {
//                            deleteEvent.postValue(draft)
//                        },
//                        onError = {
//                            //                            errorEvent.postValue(it.message)
//                        }
//                )
//                .lifeCycle()
//    }


}