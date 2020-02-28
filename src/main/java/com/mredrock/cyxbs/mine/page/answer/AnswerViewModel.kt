package com.mredrock.cyxbs.mine.page.answer

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
import com.mredrock.cyxbs.mine.network.model.AnswerDraft
import com.mredrock.cyxbs.mine.network.model.AnswerPosted
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.disposeAll
import com.mredrock.cyxbs.mine.util.extension.normalWrapper
import com.mredrock.cyxbs.mine.util.ui.RvFooter
import io.reactivex.disposables.Disposable

/**
 * Created by zia on 2018/9/10.
 */
class AnswerViewModel : BaseViewModel() {
    private val stuNum = ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()
    private val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")

    private val disposableForPosted: MutableList<Disposable> = mutableListOf()
    private val disposableForDraft: MutableList<Disposable> = mutableListOf()

    private var answerPostedPage: Int = 1
    private var answerDraftPage: Int = 1

    private val pageSize = 6

    //answerPosted部分
    private val _eventOnAnswerPosted = SingleLiveEvent<RvFooter.State>()
    val eventOnAnswerPosted: LiveData<RvFooter.State>
        get() = _eventOnAnswerPosted

    private val _answerPosted = MutableLiveData<MutableList<AnswerPosted>>()
    val answerPosted: LiveData<List<AnswerPosted>>
        get() = Transformations.map(_answerPosted) {
            it.toList()
        }

    fun loadAnswerPostedList() {
        val disposable = apiService.getAnswerPostedList(stuNum, idNum
                ?: return, answerPostedPage++, pageSize)
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            //由于Rxjava反射不应定能够够保证为空，当为空的说明这一页没有数据，于是停止加载
                            if (it.isEmpty()) {
                                if (_answerPosted.value.isNullOrEmpty()) {
                                    _eventOnAnswerPosted.postValue(RvFooter.State.NOTHING)
                                    return@safeSubscribeBy
                                } else {
                                    _eventOnAnswerPosted.postValue(RvFooter.State.NOMORE)
                                    return@safeSubscribeBy
                                }
                            }

                            val localAnswerPosted = _answerPosted.value ?: mutableListOf()
                            localAnswerPosted.addAll(it)
                            _answerPosted.postValue(localAnswerPosted)
                            //下一页
                            loadAnswerPostedList()
                        },
                        onError = {
                            _eventOnAnswerPosted.postValue(RvFooter.State.ERROR)
                        })
                .lifeCycle()
        //请求的添加，刷新的时候要取消已经发送的网络请求
        disposableForPosted.add(disposable)
    }

    fun cleanAnswerPostedPage() {
        //清除还在请求网络的接口,
        //如果一直刷新，那么前一个网络请求没有cancel掉，那么就会导致多的item
        disposeAll(disposableForPosted)

        answerPostedPage = 1
        _answerPosted.value = mutableListOf()
    }


    //answerDraft部分
    private val _eventOnAnswerDraft = SingleLiveEvent<RvFooter.State>()
    val eventOnAnswerDraft: LiveData<RvFooter.State>
        get() = _eventOnAnswerDraft

    private val _answerDraft = MutableLiveData<MutableList<AnswerDraft>>()
    val answerDraft: LiveData<List<AnswerDraft>>
        get() = Transformations.map(_answerDraft) {
            it.toList()
        }

    fun loadAnswerDraftList() {
        val disposable = apiService.getAnswerDraftList(stuNum, idNum
                ?: return, answerDraftPage++, pageSize)
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            //由于Rxjava反射不应定能够够保证为空，当为空的说明这一页没有数据，于是停止加载
                            if (it.isEmpty()) {
                                if (_answerDraft.value.isNullOrEmpty()) {
                                    _eventOnAnswerDraft.postValue(RvFooter.State.NOTHING)
                                    return@safeSubscribeBy
                                } else {
                                    _eventOnAnswerDraft.postValue(RvFooter.State.NOMORE)
                                    return@safeSubscribeBy
                                }
                            }

                            val localAnswerDraft = _answerDraft.value ?: mutableListOf()
                            localAnswerDraft.addAll(it)
                            _answerDraft.postValue(localAnswerDraft)
                            //下一页
                            loadAnswerDraftList()
                        },
                        onError = {
                            _eventOnAnswerDraft.postValue(RvFooter.State.ERROR)
                        })
                .lifeCycle()
        disposableForDraft.add(disposable)
    }

    fun cleanAnswerDraftPage() {
        //清除还在请求网络的接口,
        //如果一直刷新，那么前一个网络请求没有cancel掉，那么就会导致多的item
        disposeAll(disposableForDraft)
        answerDraftPage = 1
        _answerDraft.value = mutableListOf()
    }
}