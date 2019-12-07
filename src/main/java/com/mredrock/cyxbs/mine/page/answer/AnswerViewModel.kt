package com.mredrock.cyxbs.mine.page.answer

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.AnswerPosted
import com.mredrock.cyxbs.mine.network.model.Draft
import com.mredrock.cyxbs.mine.util.apiService
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
        apiService.getDraftList(user!!.stuNum!!, user!!.idNum!!, answerDraftPage++, pageSize)
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
                                it.type == "answer"
                            }
                            answerDraftEvent.postValue(askDraftList)
                        },
                        onError = {
                            it.printStackTrace()
                            errorEvent.postValue(it.message)
                        }
                )
                .lifeCycle()
    }
}