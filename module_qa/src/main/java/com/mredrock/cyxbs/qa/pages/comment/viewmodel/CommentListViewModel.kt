package com.mredrock.cyxbs.qa.pages.comment.viewmodel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.checkError
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.AnswerDetail
import com.mredrock.cyxbs.qa.bean.Comment
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.comment.model.CommentDataSource

class CommentListViewModel(private val answerId: String) : BaseViewModel() {
    private var factory: CommentDataSource.Factory? = null
    var commentList: LiveData<PagedList<Comment>>? = null
    var networkState: LiveData<Int>? = null
    var initialLoad: LiveData<Int>? = null
    val questionData = MutableLiveData<Question>()

    val answerLiveData = MutableLiveData<AnswerDetail>()
    private val isPraised = Transformations.map(this.answerLiveData) { it.answerIsPraised }
    private val praiseCount = Transformations.map(this.answerLiveData) { it.praiseNum }

    val praiseEvent = SingleLiveEvent<Boolean>()
    val reportAnswerEvent = MutableLiveData<Boolean>()
    val backAndRefreshAnswerEvent = SingleLiveEvent<Boolean>()
    val backAndRefreshAnswerAdoptedEvent = SingleLiveEvent<Boolean>()
    private var praiseNetworkState = NetworkState.SUCCESSFUL

    private var qid = ""

    //防止点赞快速点击
    var isDealing = false

    init {
        ApiGenerator.getApiService(ApiService::class.java)
                .getAnswer(answerId)
                .setSchedulers()
                .mapOrThrowApiException()
                .safeSubscribeBy { data ->
                    val initNum = if (data.commentNum in 1..5) data.commentNum else 6
                    val config = PagedList.Config.Builder()
                            .setEnablePlaceholders(false)
                            .setPrefetchDistance(3)
                            .setPageSize(initNum)
                            .setInitialLoadSizeHint(initNum)
                            .build()
                    factory = CommentDataSource.Factory(data.answerId)
                    commentList = LivePagedListBuilder<Int, Comment>(factory!!, config).build()
                    networkState = Transformations.switchMap(factory!!.commentDataSourceLiveData) { it.networkState }
                    initialLoad = Transformations.switchMap(factory!!.commentDataSourceLiveData) { it.initialLoad }
                    answerLiveData.value = data
                    qid = data.questionId
                }
    }

    fun invalidateCommentList() = factory?.commentDataSourceLiveData?.value?.invalidate()

    fun retryFailedListRequest() = factory?.commentDataSourceLiveData?.value?.retry()

    fun adoptAnswer(aId: String) {
        ApiGenerator.getApiService(ApiService::class.java)
                .adoptAnswer(aId, qid)
                .checkError()
                .setSchedulers()
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .safeSubscribeBy {
                    answerLiveData.value = answerLiveData.value?.apply { hasAdopted = 1 }
                    backAndRefreshAnswerAdoptedEvent.value = true
                }
    }

    fun getQuestionInfo() {
        ApiGenerator.getApiService(ApiService::class.java)
                .getQuestion(qid)
                .setSchedulers()
                .doOnError {
                    toastEvent.value = R.string.qa_answer_load_draft_question_failed
                }
                .safeSubscribeBy {
                    questionData.value = it.data
                }
    }

    fun reportAnswer(reportType: String) {
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

    fun reportComment(reportType: String, commentId: String) {
        progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT
        ApiGenerator.getApiService(ApiService::class.java)
                .reportComment(commentId, reportType)
                .setSchedulers()
                .checkError()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnError { toastEvent.value = R.string.qa_service_error_hint }
                .safeSubscribeBy {
                    toastEvent.value = R.string.qa_hint_report_success
                    reportAnswerEvent.value = true

                }
    }

    fun clickPraiseButton() {
        fun Boolean.toInt() = 1.takeIf { this@toInt } ?: -1

        if (praiseNetworkState == NetworkState.LOADING) {
            toastEvent.value = R.string.qa_comment_list_comment_loading_hint
            return
        }

        val answer = answerLiveData.value!!

        ApiGenerator.getApiService(ApiService::class.java)
                .run {
                    if (answer.answerIsPraised) {
                        cancelPraiseAnswer(answer.answerId)
                    } else {
                        praiseAnswer(answer.answerId)
                    }
                }
                .doOnSubscribe { isDealing = true }
                .checkError()
                .setSchedulers()
                .doOnSubscribe {
                    val state = !isPraised.value!!
                    (isPraised as MutableLiveData).value = state
                    (praiseCount as MutableLiveData).value = answer.praiseNum + state.toInt()
                }
                .doOnError {
                    (isPraised as MutableLiveData).value = answer.answerIsPraised
                    (praiseCount as MutableLiveData).value = answer.praiseNum
                    toastEvent.value = R.string.qa_service_error_hint
                    isDealing = false
                }
                .doFinally { praiseNetworkState = NetworkState.SUCCESSFUL }
                .safeSubscribeBy {
                    isDealing = false
                    answer.apply {
                        val state = !answer.answerIsPraised
                        praiseNum = answer.praiseNum + state.toInt()
                    }
                    praiseEvent.value = true
                    backAndRefreshAnswerEvent.value = true
                }
                .lifeCycle()
    }

    fun sendComment(content: String) {
        if (content.isBlank()) {
            longToastEvent.value = R.string.qa_comment_dialog_error_input_hint
            return
        }
        val answer = answerLiveData.value ?: return
        ApiGenerator.getApiService(ApiService::class.java)
                .sendComment(answer.answerId, content)
                .checkError()
                .setSchedulers()
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .safeSubscribeBy {
                    answerLiveData.value = answer.apply { commentNum += 1 }
                    invalidateCommentList()
                    backAndRefreshAnswerEvent.value = true
                }
                .lifeCycle()
    }

    class Factory(private val qid: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(CommentListViewModel::class.java)) {
                return CommentListViewModel(qid) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}
