package com.mredrock.cyxbs.qa.pages.question.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.pages.question.model.QuestionDataSource

/**
 * Created By jay68 on 2018/8/26.
 */
class QuestionListViewModel(kind: String) : BaseViewModel() {
    val questionList: LiveData<PagedList<Question>>
    val networkState: LiveData<Int>
    val initialLoad: LiveData<Int>

    private val factory: QuestionDataSource.Factory

    init {
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(3)
                .setPageSize(6)
                .setInitialLoadSizeHint(6)
                .build()
        factory = QuestionDataSource.Factory(kind)
        questionList = LivePagedListBuilder<Int, Question>(factory, config).build()
        networkState = Transformations.switchMap(factory.questionDataSourceLiveData) { it.networkState }
        initialLoad = Transformations.switchMap(factory.questionDataSourceLiveData) { it.initialLoad }
    }

    fun invalidateQuestionList() = questionList.value?.dataSource?.invalidate()

    fun retry() = factory.questionDataSourceLiveData.value?.retry()

    class Factory(private val kind: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(QuestionListViewModel::class.java)) {
                return QuestionListViewModel(kind) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}