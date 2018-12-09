package com.mredrock.cyxbs.qa.pages.answer.viewmodel

import android.arch.lifecycle.*
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.checkError
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.pages.answer.model.AnswerDataSource

/**
 * Created By jay68 on 2018/9/27.
 */
class AnswerListViewModel(question: Question) : BaseViewModel() {
    val bottomViewEvent = MutableLiveData<Boolean?>()
    val answerList: LiveData<PagedList<Answer>>
    val questionLiveData = MutableLiveData<Question>()
    val backAndRefreshPreActivityEvent = SingleLiveEvent<Boolean>()

    val networkState: LiveData<Int>
    val initialLoad: LiveData<Int>

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
        answerList = LivePagedListBuilder<Int, Answer>(factory, config).build()
        networkState = Transformations.switchMap(factory.answerDataSourceLiveData) { it.networkState }
        initialLoad = Transformations.switchMap(factory.answerDataSourceLiveData) { it.initialLoad }

        questionLiveData.value = question
        bottomViewEvent.value = question.isSelf.takeUnless { question.hasAdoptedAnswer }
    }

    fun invalidate() = factory.answerDataSourceLiveData.value?.invalidate()

    fun retry() = factory.answerDataSourceLiveData.value?.retry()

    fun addReward() {
        //todo 加价
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
        //todo 忽略
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