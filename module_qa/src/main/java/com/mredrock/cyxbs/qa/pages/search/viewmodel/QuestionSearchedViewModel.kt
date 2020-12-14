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

import com.mredrock.cyxbs.qa.beannew.Knowledge
import com.mredrock.cyxbs.qa.beannew.Question
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.SearchResult
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.search.model.SearchQuestionDataSource


/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class QuestionSearchedViewModel(var searchKey: String) : BaseViewModel() {
    //此属性暂时未使用
    val questionList: LiveData<PagedList<Question>>

    val initialLoad=MutableLiveData<Int>()
    var searchedDynamic=MutableLiveData<List<Dynamic>>()
    var knowledge = MutableLiveData<List<Knowledge>>()
    var SEARCH_RESULT_IS_EMPTY=false//判断搜索结果的有无
    var isCreateOver=MutableLiveData<Boolean>()//判断是否网络请求参数完成
    var isKnowledge=false//判断知识库的结果的有无
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
    }

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

    fun getSearchDynamic(key: String){
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getSearchResult(key)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnSubscribe {initialLoad.value=NetworkState.LOADING  }
                .doOnError {
                    LogUtils.d("zt",it.message.toString())
                    initialLoad.value = NetworkState.FAILED
                }
                .safeSubscribeBy {
                    initialLoad.value=NetworkState.SUCCESSFUL
                    if (it.knowledge.isNotEmpty()){
                        knowledge.value=it.knowledge
                        isKnowledge=true
                    }else{
                        isKnowledge=false
                    }
                    if (it.dynamicList.isNotEmpty()){
                        searchedDynamic.value=it.dynamicList
                    }
                    if (it.dynamicList.isNotEmpty()||it.knowledge.isNotEmpty()){
                        SEARCH_RESULT_IS_EMPTY=false
                    }else{
                        SEARCH_RESULT_IS_EMPTY=true
                    }
                    isCreateOver.value=true
                    LogUtils.d("zt",it.dynamicList.toString())
                }
    }
}