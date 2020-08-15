package com.mredrock.cyxbs.qa.pages.search.viewmodel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.bean.Knowledge
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.pages.search.model.SearchQuestionDataSource


/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class QuestionSearchedViewModel(var searchKey: String) : BaseViewModel() {
    val questionList: LiveData<PagedList<Question>>
    val networkState: LiveData<Int>
    val initialLoad: LiveData<Int>

    var knowledge = MutableLiveData<Knowledge>()
    private val factory: SearchQuestionDataSource.Factory

    init {
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(3)
                .setPageSize(6)
                .setInitialLoadSizeHint(6)
                .build()
        factory = SearchQuestionDataSource.Factory(searchKey)
        questionList = LivePagedListBuilder<Int, Question>(factory, config).build()
        networkState = Transformations.switchMap(factory.searchQuestionDataSourceLiveData) { it.networkState }
        initialLoad = Transformations.switchMap(factory.searchQuestionDataSourceLiveData) { it.initialLoad }
    }

    fun invalidateQuestionList() = questionList.value?.dataSource?.invalidate()

    fun retry() = factory.searchQuestionDataSourceLiveData.value?.retry()

    class Factory(private val searchKey: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(QuestionSearchedViewModel::class.java)) {
                return QuestionSearchedViewModel(searchKey) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }

    fun getKnowledge(key: String) {
        ApiGenerator.getApiService(ApiService::class.java)
                .getKnowledge(key)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnSubscribe { }
                .doOnError {

                }
                .safeSubscribeBy { data ->
                    knowledge.value = data
                }

    }
}