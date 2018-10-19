package com.mredrock.cyxbs.qa.pages.answer.viewmodel

import android.arch.lifecycle.*
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.pages.answer.model.AnswerDataSource

/**
 * Created By jay68 on 2018/9/27.
 */
class AnswerListViewModel(question: Question) : BaseViewModel() {
    val bottomViewEvent = MutableLiveData<Boolean?>()
    val answerList: LiveData<PagedList<Answer>>
    val questionLiveDate = MutableLiveData<Question>()

    val networkState: LiveData<Int>
    val initialLoad: LiveData<Int>

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

        questionLiveDate.value = question
    }

    fun invalidate() = factory.answerDataSourceLiveData.value?.invalidate()

    fun retry() = factory.answerDataSourceLiveData.value?.retry()

    fun addReward() {

    }

    fun cancelQuestion() {

    }

    fun ignoreQuestion() {

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