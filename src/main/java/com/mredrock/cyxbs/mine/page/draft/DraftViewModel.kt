package com.mredrock.cyxbs.mine.page.draft

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IUserService
import com.mredrock.cyxbs.common.service.account.IUserStateService
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.Draft
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.normalStatus
import com.mredrock.cyxbs.mine.util.user

/**
 * Created by zia on 2018/9/10.
 */
class DraftViewModel : BaseViewModel() {

    val errorEvent = MutableLiveData<String>()
    val draftEvent = MutableLiveData<List<Draft>>()
    val deleteEvent = MutableLiveData<Draft>()
    val sendMessageEvent = MutableLiveData<Draft>()

    private val stuNum = ServiceManager.getService(IUserService::class.java).getStuNum()
    private val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")

    private val userStateService: IUserStateService by lazy {
        ServiceManager.getService(IUserStateService::class.java)
    }

    @Volatile
    var page = 1
    private val pageSize = 6

    fun loadDraftList() {
        if (!userStateService.isLogin()) {
            return
        }
        apiService.getDraftList(stuNum, idNum, page++, pageSize)
                .mapOrThrowApiException()
                .map { list ->
                    list.forEach { it.parseQuestion() }
                    list
                }
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .safeSubscribeBy(
                        onNext = {
                            draftEvent.postValue(it)
                        },
                        onError = {
                            it.printStackTrace()
                            errorEvent.postValue(it.message)
                        }
                )
                .lifeCycle()
    }

    fun deleteDraft(draft: Draft) {
        if (!userStateService.isLogin()) {
            return
        }
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

    fun sendRemark(draft: Draft, content: String?) {
        if (!userStateService.isLogin()) {
            return
        }
        val s = content ?: return
        apiService.commentAnswer(stuNum, idNum, draft.targetId, s)
                .normalStatus(this)
                .safeSubscribeBy(
                        onNext = {
                            sendMessageEvent.postValue(draft)
                        },
                        onError = {
                            errorEvent.postValue(it.message)
                        }
                )
                .lifeCycle()
        deleteDraft(draft)
    }

    fun cleanPage() {
        page = 1
    }
}