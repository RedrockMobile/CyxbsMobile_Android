package com.mredrock.cyxbs.qa.pages.question.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.qa.bean.HotQA
import com.mredrock.cyxbs.qa.pages.question.model.FreshManQuestionDataSource

/**
 * Created by yyfbe, Date on 2020/8/11.
 * 用于迎新生问题加载
 */
class FreshManQuestionListViewModel(kind: String) : QuestionListViewModel(kind) {
    val freshManQuestionList: LiveData<PagedList<HotQA>>
    val freshManNetworkState: LiveData<Int>
    val freshManInitialLoad: LiveData<Int>

    private val factory: FreshManQuestionDataSource.Factory

    init {
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(3)
                .setPageSize(6)
                .setInitialLoadSizeHint(6)
                .build()
        factory = FreshManQuestionDataSource.Factory()
        freshManQuestionList = LivePagedListBuilder<Int, HotQA>(factory, config).build()
        freshManNetworkState = Transformations.switchMap(factory.freshManQuestionDataSourceLiveData) { it.networkState }
        freshManInitialLoad = Transformations.switchMap(factory.freshManQuestionDataSourceLiveData) { it.initialLoad }
    }

    fun invalidateFreshManQuestionList() = freshManQuestionList.value?.dataSource?.invalidate()

    fun freshManRetry() = factory.freshManQuestionDataSourceLiveData.value?.retry()

    class Factory(private val kind: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(FreshManQuestionListViewModel::class.java)) {
                return FreshManQuestionListViewModel(kind) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}