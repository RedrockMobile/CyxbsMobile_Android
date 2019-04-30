package com.mredrock.cyxbs.qa.pages.answer.viewmodel

import android.arch.lifecycle.*
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
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

    private val answerList: LiveData<List<Answer>>
    val networkState: LiveData<Int>
    val initialLoad: LiveData<Int>
    var myRewardCount = 0

    val qid get() = questionLiveData.value!!.id

    private val factory: AnswerDataSource.Factory

    init {
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(3)
                .setPageSize(6)
                .setInitialLoadSizeHint(6)
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
        answerPagedList.observe(this, Observer {  })
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
                        (questionLiveData.value?.reward ?: 0 + reward).toString())
                .setSchedulers()
                .checkError()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnError { BaseApp.context.longToast(it.message!!) }
                .safeSubscribeBy { backAndRefreshPreActivityEvent.value = true }

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
        val qid = questionLiveData.value!!.id
        ApiGenerator.getApiService(ApiService::class.java)
                .ignoreQuestion(user.stuNum!!, user.idNum!!, qid)
                .setSchedulers()
                .checkError()
                .safeSubscribeBy { backAndRefreshPreActivityEvent.value = true }
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