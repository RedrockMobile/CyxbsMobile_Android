package com.mredrock.cyxbs.qa.pages.answer.viewmodel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.BaseApp
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
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.answer.model.AnswerDataSource
import org.jetbrains.anko.longToast

/**
 * Created By jay68 on 2018/9/27.
 */
class AnswerListViewModel(question: Question) : BaseViewModel() {
    val bottomViewEvent = MutableLiveData<Boolean?>()
    private val answerPagedList: LiveData<PagedList<Answer>>
    val questionLiveData = MutableLiveData<Question>()
    val backAndRefreshPreActivityEvent = SingleLiveEvent<Boolean>()
    val backPreActivityReportQuestionEvent = SingleLiveEvent<Boolean>()
    val backPreActivityReportAnswerEvent = SingleLiveEvent<Boolean>()
    val backPreActivityIgnoreEvent = SingleLiveEvent<Boolean>()

    private val answerList: LiveData<List<Answer>>
    val networkState: LiveData<Int>
    val initialLoad: LiveData<Int>
    var myRewardCount = 0

    private var praiseNetworkState = NetworkState.SUCCESSFUL
    val refreshPreActivityEvent = SingleLiveEvent<Int>()
    val qid get() = questionLiveData.value!!.id

    private val factory: AnswerDataSource.Factory

    init {
        val initNum = if (question.answerNum == 0) 6 else question.answerNum
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(3)
                .setPageSize(initNum)
                .setInitialLoadSizeHint(initNum)
                .build()
        factory = AnswerDataSource.Factory(question.id)
        answerPagedList = LivePagedListBuilder<Int, Answer>(factory, config).build()
        answerList = Transformations.switchMap(factory.answerDataSourceLiveData) { it.answerList }
        networkState = Transformations.switchMap(factory.answerDataSourceLiveData) { it.networkState }
        initialLoad = Transformations.switchMap(factory.answerDataSourceLiveData) { it.initialLoad }

        questionLiveData.value = question
        bottomViewEvent.value = question.isSelf.takeUnless { question.hasAdoptedAnswer }
    }

    fun LifecycleOwner.observeAnswerList(onChange: Observer<List<Answer>>) {
        answerList.observe(this, onChange)
        answerPagedList.observe(this, Observer { })
    }
    //增加浏览量，不用显示
    fun addQuestionView() {
        ApiGenerator.getApiService(ApiService::class.java)
                .addView(BaseApp.user?.stuNum ?: "",
                        BaseApp.user?.idNum ?: "",
                        qid)
                .checkError()
                .setSchedulers()
                .doOnError {
                    LogUtils.d("add QuestionView Failed", it.toString())
                }
                .safeSubscribeBy {
                    LogUtils.d("add QuestionView Success", it.toString())
                }
    }

    fun adoptAnswer(aId: String) {
        ApiGenerator.getApiService(ApiService::class.java)
                .adoptAnswer(aId, qid,
                        BaseApp.user?.stuNum ?: "",
                        BaseApp.user?.idNum ?: "")
                .checkError()
                .setSchedulers()
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .safeSubscribeBy {
                    backAndRefreshPreActivityEvent.value = true
                }
    }

    fun reportQuestion(reportType: String) {
        progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT
        val user = BaseApp.user ?: return
        ApiGenerator.getApiService(ApiService::class.java)
                .reportQuestion(user.stuNum ?: "", user.idNum ?: "", qid, reportType)
                .setSchedulers()
                .checkError()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnError { toastEvent.value = R.string.qa_service_error_hint }
                .safeSubscribeBy {
                    toastEvent.value = R.string.qa_hint_report_success
                    backPreActivityReportQuestionEvent.value = true
                }
    }

    fun reportAnswer(reportType: String, answerId: String) {
        progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT
        val user = BaseApp.user ?: return
        ApiGenerator.getApiService(ApiService::class.java)
                .reportAnswer(user.stuNum ?: "", user.idNum ?: "", answerId, reportType)
                .setSchedulers()
                .checkError()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnError { toastEvent.value = R.string.qa_service_error_hint }
                .safeSubscribeBy {
                    toastEvent.value = R.string.qa_hint_report_success
                    backPreActivityReportAnswerEvent.value = true
                }
    }

    fun getMyReward() {
        val user = BaseApp.user ?: return
        ApiGenerator.getApiService(ApiService::class.java)
                .getScoreStatus(user.stuNum ?: "", user.idNum ?: "")
                .setSchedulers()
                .mapOrThrowApiException()
                .safeSubscribeBy { myRewardCount = it.integral }
    }

    fun invalidate() = factory.answerDataSourceLiveData.value?.invalidate()

    fun retry() = factory.answerDataSourceLiveData.value?.retry()

    fun addReward(reward: Int): Boolean {
        if (reward > myRewardCount) {
            longToastEvent.value = R.string.qa_quiz_error_reward_not_enough
            return false
        }
        val user = BaseApp.user ?: return true
        progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT

        ApiGenerator.getApiService(ApiService::class.java)
                .updateReward(user.stuNum!!, user.idNum!!, qid,
                        ((questionLiveData.value?.reward ?: 0) + reward).toString())
                .setSchedulers()
                .checkError()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnError { BaseApp.context.longToast(it.message!!) }
                .safeSubscribeBy { backAndRefreshPreActivityEvent.value = true }
                .lifeCycle()

        return true
    }

    fun cancelQuestion() {
        val user = BaseApp.user ?: return
        val qid = questionLiveData.value!!.id
        ApiGenerator.getApiService(ApiService::class.java)
                .cancelQuestion(user.stuNum!!, user.idNum!!, qid)
                .setSchedulers()
                .checkError()
                .safeSubscribeBy { backAndRefreshPreActivityEvent.value = true }
    }

    fun ignoreQuestion() {
        val user = BaseApp.user ?: return
        val qid = questionLiveData.value?.id
        LogUtils.d("ignoreInfo", qid.toString())
        ApiGenerator.getApiService(ApiService::class.java)
                .ignoreQuestion(user.stuNum ?: "", user.idNum ?: "", qid ?: "")
                .setSchedulers()
                .checkError()
                .doOnError { toastEvent.value = R.string.qa_service_error_hint }
                .safeSubscribeBy { backPreActivityIgnoreEvent.value = true }
    }

    fun clickPraiseButton(position: Int, answer: Answer) {
        fun Boolean.toInt() = 1.takeIf { this@toInt } ?: -1

        if (praiseNetworkState == NetworkState.LOADING) {
            toastEvent.value = R.string.qa_comment_list_comment_loading_hint
            return
        }
        val user = BaseApp.user ?: return
        ApiGenerator.getApiService(ApiService::class.java)
                .run {
                    if (answer.isPraised) {
                        cancelPraiseAnswer(answer.id, user.stuNum ?: "", user.idNum ?: "")
                    } else {
                        praiseAnswer(answer.id, user.stuNum ?: "", user.idNum ?: "")
                    }
                }
                .checkError()
                .setSchedulers()
                .doOnError {
                    toastEvent.value = R.string.qa_service_error_hint
                }
                .doFinally {
                    praiseNetworkState = NetworkState.SUCCESSFUL
                }
                .safeSubscribeBy {
                    answer.apply {
                        val state = !isPraised
                        praiseNum = "${answer.praiseNumInt + state.toInt()}"
                        isPraised = state
                    }
                    refreshPreActivityEvent.value = position
                }
                .lifeCycle()
    }

    class Factory(private val question: Question) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(AnswerListViewModel::class.java)) {
                return AnswerListViewModel(question) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}