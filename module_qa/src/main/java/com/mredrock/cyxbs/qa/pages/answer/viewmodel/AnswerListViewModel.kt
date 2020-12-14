package com.mredrock.cyxbs.qa.pages.answer.viewmodel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.checkError
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.beannew.Question
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.answer.model.AnswerDataSource

/**
 * Created By jay68 on 2018/9/27.
 */
class AnswerListViewModel(private val questionId: String) : BaseViewModel() {
    val bottomViewEvent = MutableLiveData<Boolean?>()
    var answerPagedList: LiveData<PagedList<Answer>>? = null
    val questionLiveData = MutableLiveData<Question>()
    val backAndRefreshPreActivityEvent = SingleLiveEvent<Boolean>()
    val reportQuestionEvent = SingleLiveEvent<Boolean>()
    val reportAnswerEvent = SingleLiveEvent<Boolean>()
    val ignoreEvent = SingleLiveEvent<Boolean>()

    var networkState: LiveData<Int>? = null
    var initialLoad: LiveData<Int>? = null

    private var praiseNetworkState = NetworkState.SUCCESSFUL
    val refreshPreActivityEvent = SingleLiveEvent<Int>()

    //防止点赞快速点击
    var isDealing = false

    private var factory: AnswerDataSource.Factory? = null

    init {
        ApiGenerator.getApiService(ApiService::class.java)
                .getQuestion(questionId)
                .setSchedulers()
                .mapOrThrowApiException()
                .safeSubscribeBy { data ->
                    //要先从问题，拿到回答数，再来配置paging加载
                    val initNum = if (data.answerNum in 1..5) data.answerNum else 6
                    val config = PagedList.Config.Builder()
                            .setEnablePlaceholders(false)
                            .setPrefetchDistance(3)
                            .setPageSize(initNum)
                            .setInitialLoadSizeHint(initNum)
                            .build()
                    factory = AnswerDataSource.Factory(questionId)
                    answerPagedList = LivePagedListBuilder<Int, Answer>(factory!!, config).build()
                    networkState = Transformations.switchMap(factory!!.answerDataSourceLiveData) { it.networkState }
                    initialLoad = Transformations.switchMap(factory!!.answerDataSourceLiveData) { it.initialLoad }

                    questionLiveData.value = data
                    bottomViewEvent.value = data.isSelf
                }

    }

    //增加浏览量，不用显示
    fun addQuestionView() {
        ApiGenerator.getApiService(ApiService::class.java)
                .addView(questionId)
                .checkError()
                .setSchedulers()
                .doOnError {
                    LogUtils.d("add QuestionView Failed", it.toString())
                }
                .safeSubscribeBy { LogUtils.d("add QuestionView Success", it.toString()) }
    }

    fun reportQuestion(reportType: String) {
        progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT
        ApiGenerator.getApiService(ApiService::class.java)
                .reportQuestion(questionId, reportType)
                .setSchedulers()
                .checkError()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnError { toastEvent.value = R.string.qa_service_error_hint }
                .safeSubscribeBy {
                    toastEvent.value = R.string.qa_hint_report_success
                    reportQuestionEvent.value = true
                }
    }

    fun reportAnswer(reportType: String, answerId: String) {
        progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT
        ApiGenerator.getApiService(ApiService::class.java)
                .reportAnswer(answerId, reportType)
                .setSchedulers()
                .checkError()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnError { toastEvent.value = R.string.qa_service_error_hint }
                .safeSubscribeBy {
                    toastEvent.value = R.string.qa_hint_report_success
                    reportAnswerEvent.value = true
                }
    }

    fun invalidate() = factory?.answerDataSourceLiveData?.value?.invalidate()

    fun retry() = factory?.answerDataSourceLiveData?.value?.retry()


    fun ignoreQuestion() {
        val qid = questionLiveData.value?.id
        ApiGenerator.getApiService(ApiService::class.java)
                .ignoreQuestion(qid ?: "")
                .setSchedulers()
                .checkError()
                .doOnError { toastEvent.value = R.string.qa_service_error_hint }
                .safeSubscribeBy {
                    toastEvent.value = R.string.qa_question_ignore_success
                    ignoreEvent.value = true
                    backAndRefreshPreActivityEvent.value = true
                }
    }

    fun clickPraiseButton(position: Int, answer: Answer) {
        fun Boolean.toInt() = 1.takeIf { this@toInt } ?: -1

        if (praiseNetworkState == NetworkState.LOADING) {
            toastEvent.value = R.string.qa_comment_list_comment_loading_hint
            return
        }
        ApiGenerator.getApiService(ApiService::class.java)
                .run {
                    if (answer.isPraised) {
                        cancelPraiseAnswer(answer.id)
                    } else {
                        praiseAnswer(answer.id)
                    }
                }
                .doOnSubscribe { isDealing = true }
                .checkError()
                .setSchedulers()
                .doOnError {
                    toastEvent.value = R.string.qa_service_error_hint
                    isDealing = false
                }
                .doFinally {
                    praiseNetworkState = NetworkState.SUCCESSFUL
                }
                .safeSubscribeBy {
                    isDealing = false
                    answer.apply {
                        val state = !isPraised
                        praiseNum = "${answer.praiseNumInt + state.toInt()}"
                        isPraised = state
                    }
                    refreshPreActivityEvent.value = position
                }
                .lifeCycle()
    }

    class Factory(private val questionId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(AnswerListViewModel::class.java)) {
                return AnswerListViewModel(questionId) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}