package com.mredrock.cyxbs.mine.page.answer

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IUserService
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.AnswerPosted
import com.mredrock.cyxbs.mine.network.model.Draft
import com.mredrock.cyxbs.mine.network.model.Question
import com.mredrock.cyxbs.mine.util.extension.logr
import com.mredrock.cyxbs.mine.util.user

/**
 * Created by zia on 2018/9/10.
 */
class AnswerViewModel : BaseViewModel() {

    val errorEvent = MutableLiveData<String>()
    //    val adoptOverEvent = MutableLiveData<List<MyHelpQuestion>>()
//    val adoptWaitEvent = MutableLiveData<List<MyHelpQuestion>>()
    val answerPostedEvent = MutableLiveData<List<AnswerPosted>>()

    val answerDraftEvent = MutableLiveData<List<Draft>>()
    val deleteEvent = MutableLiveData<Draft>()

    private val pageSize = 6
    private var answerDraftPage = 1

    private val stuNum = ServiceManager.getService(IUserService::class.java).getStuNum()
    private val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")

//    fun loadAdoptOver() {
//        apiService.getMyHelpOver(user!!.stuNum!!, user!!.idNum!!, overPage++, pageSize)
//                .normalWrapper(this)
//                .safeSubscribeBy(
//                        onNext = {
//                            adoptOverEvent.postValue(it)
//                        },
//                        onError = {
//                            errorEvent.postValue(it.message)
//                        }
//                )
//                .lifeCycle()
//    }
//
//    fun loadAdoptWait() {
//        apiService.getMyHelpWait(user!!.stuNum!!, user!!.idNum!!, waitPage++, pageSize)
//                .normalWrapper(this)
//                .safeSubscribeBy(
//                        onNext = {
//                            adoptWaitEvent.postValue(it)
//                        },
//                        onError = {
//                            errorEvent.postValue(it.message)
//                        }
//                )
//                .lifeCycle()
//    }

    fun cleanPage() {
        answerDraftPage = 1
    }


    fun loadAnswerDraftList() {
//        apiService.getDraftList(stuNum, idNum, answerDraftPage++, pageSize)
//                .mapOrThrowApiException()
//                .map { list ->
//                    list.forEach { it.parseQuestion() }
//                    list
//                }
//                .setSchedulers()
//                .doOnErrorWithDefaultErrorHandler { false }
//                .safeSubscribeBy(
//                        onNext = { it ->
//                            val askDraftList = it.filter {
//                                it.type == "answer"
//                            }
//                            answerDraftEvent.postValue(askDraftList)
//                        },
//                        onError = {
//                            it.printStackTrace()
//                            errorEvent.postValue(it.message)
//                        }
//                )
//                .lifeCycle()
        if (answerDraftPage == 3) {
            answerDraftEvent.postValue(listOf())
            return
        }
        val answerDraft = Draft("1", "abc", "roger", "answer", "标题", "内容在此" + Math.random(), Question());
        val answerDraft1 = Draft("2", "abc", "roger", "answer", "标题", "内容在此" + Math.random(), Question());
        val answerDraft2 = Draft("3", "abc", "roger", "answer", "标题", "内容在此" + Math.random(), Question());
        val answerDraft3 = Draft("4", "abc", "roger", "answer", "标题", "内容在此" + Math.random(), Question());
        val answerDraft4 = Draft("5", "abc", "roger", "answer", "标题", "内容在此" + Math.random(), Question());
        val list = mutableListOf<Draft>(answerDraft, answerDraft1, answerDraft2, answerDraft3, answerDraft4)
        answerDraftEvent.postValue(list.toList())
        answerDraftPage++
    }

    fun deleteDraft(draft: Draft) {
//        apiService.deleteDraft(stuNum, idNum, draft.id)
//                .checkError()
//                .setSchedulers()
//                .doOnErrorWithDefaultErrorHandler { false }
//                .safeSubscribeBy(
//                        onNext = {
//                            deleteEvent.postValue(draft)
//                        },
//                        onError = {
//                            errorEvent.postValue(it.message)
//                        }
//                )
//                .lifeCycle()
        deleteEvent.postValue(draft)
        logr("this is viewmodel delete")
    }
}