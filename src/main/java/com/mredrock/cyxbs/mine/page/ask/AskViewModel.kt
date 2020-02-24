package com.mredrock.cyxbs.mine.page.ask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.mine.network.model.AskDraft
import com.mredrock.cyxbs.mine.network.model.AskPosted
import com.mredrock.cyxbs.mine.network.model.NavigateData
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.normalWrapper
import com.mredrock.cyxbs.mine.util.ui.RvFooter

/**
 * Created by zia on 2018/9/10.
 */
class AskViewModel : BaseViewModel() {

    private val stuNum = ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()
    private val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")

    private var askDraftPage: Int = 1
    private var askPostedPage: Int = 1

    private val pageSize = 6

    //askPosted部分
    private val _eventOnAskPosted = SingleLiveEvent<RvFooter.State>()
    val eventOnAskPosted: LiveData<RvFooter.State>
        get() = _eventOnAskPosted

    private val _askPosted = MutableLiveData<MutableList<AskPosted>>()
    val askPosted: LiveData<List<AskPosted>>
        get() = Transformations.map(_askPosted) {
            it.toList()
        }

    fun loadAskPostedList() {
        apiService.getAskPostedList(stuNum, idNum
                ?: return, askPostedPage++, pageSize)
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            if (it.isEmpty()) {
                                if (_askPosted.value.isNullOrEmpty()) {
                                    _eventOnAskPosted.postValue(RvFooter.State.NOTHING)
                                    return@safeSubscribeBy
                                } else {
                                    _eventOnAskPosted.postValue(RvFooter.State.NOMORE)
                                    return@safeSubscribeBy
                                }
                            }

                            val localAskPosted = _askPosted.value ?: mutableListOf()
                            localAskPosted.addAll(it)
                            _askPosted.postValue(localAskPosted)
                        },
                        onError = {
                            _eventOnAskPosted.postValue(RvFooter.State.ERROR)
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

    //askDraft部分
    private val _eventOnAskDraft = SingleLiveEvent<RvFooter.State>()
    val eventOnAskDraft: LiveData<RvFooter.State>
        get() = _eventOnAskDraft

    private val _askDraft = MutableLiveData<MutableList<AskDraft>>()
    val askDraft: LiveData<List<AskDraft>>
        get() = Transformations.map(_askDraft) {
            it.toList()
        }

    fun loadAskDraftList() {
        apiService.getAskDraftList(stuNum, idNum
                ?: return, askDraftPage++, pageSize)
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            //由于Rxjava反射不应定能够够保证为空，当为空的说明这一页没有数据，于是停止加载
                            if (it.isEmpty()) {
                                if (_askDraft.value.isNullOrEmpty()) {
                                    _eventOnAskDraft.postValue(RvFooter.State.NOTHING)
                                    return@safeSubscribeBy
                                } else {
                                    _eventOnAskDraft.postValue(RvFooter.State.NOMORE)
                                    return@safeSubscribeBy
                                }
                            }

                            val localAskDraft = _askDraft.value ?: mutableListOf()
                            localAskDraft.addAll(it)
                            _askDraft.postValue(localAskDraft)
                        },
                        onError = {
                            _eventOnAskDraft.postValue(RvFooter.State.ERROR)
                        })
                .lifeCycle()
    }

    fun cleanAskDraftPage() {
        //清除还在请求网络的接口,
        //如果一直刷新，那么前一个网络请求没有cancel掉，那么就会导致多的item
        onCleared()

        askDraftPage = 1
        _askDraft.value = mutableListOf()
    }

    //Question跳转请求的对象
    val navigateEvent = SingleLiveEvent<NavigateData>()

    fun getQuestion(qid: Int) {
        val idNum = idNum ?: return
        apiService.getQuestion(stuNum, idNum, qid.toString())
                .setSchedulers()
                .safeSubscribeBy {
                    val navigateData = NavigateData(qid, it.string())
                    navigateEvent.postValue(navigateData)
                }
                .lifeCycle()
    }
}