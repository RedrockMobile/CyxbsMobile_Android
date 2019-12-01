package com.mredrock.cyxbs.mine.page.draft

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.Draft
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.log
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

    @Volatile
    var page = 1
    private val pageSize = 6

    fun loadDraftList() {
        if (!isLogin()) {
            return
        }
        log("loadDraftList page:$page")
        apiService.getDraftList(user!!.stuNum!!, user!!.idNum!!, page++, pageSize)
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
        if (!isLogin()) {
            return
        }
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

    fun sendRemark(draft: Draft, content: String?) {
        if (!isLogin()) {
            return
        }
        val s = content ?: return
        apiService.commentAnswer(user!!.stuNum!!, user!!.idNum!!, draft.targetId,s)
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

    private fun isLogin(): Boolean {
        if (user == null) {
            errorEvent.postValue("没有登录..")
            return false
        }
        return true
    }

    fun cleanPage() {
        page = 1
    }
}