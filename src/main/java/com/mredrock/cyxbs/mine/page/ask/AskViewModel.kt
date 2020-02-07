package com.mredrock.cyxbs.mine.page.ask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.mine.network.model.AskPosted
import com.mredrock.cyxbs.mine.network.model.Draft
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.user

/**
 * Created by zia on 2018/9/10.
 */
class AskViewModel : BaseViewModel() {

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
        if (askPostedPage++ == 10) {
            _eventOnAskPosted.value = true
            return
        }
        val ask = AskPosted(12, "title", "描述", "时间", 12, "未解决")

        val data = _askPosted.value ?: mutableListOf()
        data.add(ask)
        data.add(ask)
        data.add(ask)
        _askPosted.value = data
    }

    fun loadAskDraftList() {
        apiService.getDraftList(user?.stuNum ?: return, user?.idNum
                ?: return, askDraftPage++, pageSize)
                .mapOrThrowApiException()
                .map { list ->
                    list.forEach { it.parseQuestion() }
                    list
                }
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .safeSubscribeBy(
                        onNext = { it ->
                            val askDraftList = it.filter {
                                it.type == "question"
                            }
                            askDraftEvent.postValue(askDraftList)
                        },
                        onError = {
                            it.printStackTrace()
//                            errorEvent.postValue(it.message)
                        }
                )
                .lifeCycle()
    }

    fun deleteDraft(draft: Draft) {
        apiService.deleteDraft(user?.stuNum ?: return, user?.idNum ?: return, draft.id)
                .checkError()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .safeSubscribeBy(
                        onNext = {
                            deleteEvent.postValue(draft)
                        },
                        onError = {
//                            errorEvent.postValue(it.message)
                        }
                )
                .lifeCycle()
    }


    fun cleanAskPostedPage() {
        askPostedPage = 1
        _askPosted.value = mutableListOf()
    }


}