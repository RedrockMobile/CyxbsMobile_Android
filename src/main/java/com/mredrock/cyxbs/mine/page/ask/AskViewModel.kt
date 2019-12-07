package com.mredrock.cyxbs.mine.page.ask

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
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

    val errorEvent = MutableLiveData<String>()
    val askPostedEvent = MutableLiveData<List<AskPosted>>()
    val askDraftEvent = MutableLiveData<List<Draft>>()
    val deleteEvent = MutableLiveData<Draft>()

    fun loadAskDraftList() {
        apiService.getDraftList(user!!.stuNum!!, user!!.idNum!!, askDraftPage++, pageSize)
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
                            errorEvent.postValue(it.message)
                        }
                )
                .lifeCycle()
    }

    fun deleteDraft(draft: Draft) {
        apiService.deleteDraft(user!!.stuNum!!, user!!.idNum!!, draft.id)
                .checkError()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .safeSubscribeBy(
                        onNext = {
                            deleteEvent.postValue(draft)
                        },
                        onError = {
                            errorEvent.postValue(it.message)
                        }
                )
                .lifeCycle()
    }


    fun cleanPage() {
        askDraftPage = 1
        askPostedPage = 1
    }
}