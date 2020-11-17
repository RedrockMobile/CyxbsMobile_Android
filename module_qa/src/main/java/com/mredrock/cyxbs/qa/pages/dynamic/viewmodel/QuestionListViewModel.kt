package com.mredrock.cyxbs.qa.pages.dynamic.viewmodel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.pages.dynamic.model.QuestionDataSource

/**
 * Created By jay68 on 2018/8/26.
 */
open class QuestionListViewModel(kind: String) : BaseViewModel() {
    val dynamicList: LiveData<PagedList<Question>>
    val networkState: LiveData<Int>
    val initialLoad: LiveData<Int>
    var hotWords = MutableLiveData<List<String>>()

    private val factory: QuestionDataSource.Factory

    init {
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(3)
                .setPageSize(6)
                .setInitialLoadSizeHint(6)
                .build()
        factory = QuestionDataSource.Factory(kind)
        dynamicList = LivePagedListBuilder<Int, Question>(factory, config).build()
        networkState = Transformations.switchMap(factory.questionDataSourceLiveData) { it.networkState }
        initialLoad = Transformations.switchMap(factory.questionDataSourceLiveData) { it.initialLoad }
    }

    fun getScrollerText() {
        ApiGenerator.getApiService(ApiService::class.java)
                .getHotWords()
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy { texts ->
                    hotWords.value = texts.scrollerHotWord
                }
    }

    fun invalidateQuestionList() = dynamicList.value?.dataSource?.invalidate()

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