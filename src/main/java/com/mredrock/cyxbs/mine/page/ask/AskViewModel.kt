package com.mredrock.cyxbs.mine.page.ask

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IUserService
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.AskPosted
import com.mredrock.cyxbs.mine.network.model.Draft
import com.mredrock.cyxbs.mine.util.apiService

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

    private val stuNum = ServiceManager.getService(IUserService::class.java).getStuNum()
    private val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")

    fun loadAskDraftList() {
        apiService.getDraftList(stuNum, idNum
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
                            errorEvent.postValue(it.message)
                        }
                )
                .lifeCycle()
    }

    fun deleteDraft(draft: Draft) {
        apiService.deleteDraft(stuNum, idNum, draft.id)
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