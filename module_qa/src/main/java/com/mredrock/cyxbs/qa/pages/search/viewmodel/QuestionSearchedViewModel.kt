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
import com.mredrock.cyxbs.qa.R

import com.mredrock.cyxbs.qa.beannew.Knowledge
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.pages.search.model.SearchQuestionDataSource


/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class QuestionSearchedViewModel(var searchKey: String) : BaseViewModel() {

    val questionList: LiveData<PagedList<Dynamic>>
    var knowledge = MutableLiveData<List<Knowledge>>()


    val ignorePeople = MutableLiveData<Boolean>()//屏蔽
    val deleteTips = MutableLiveData<Boolean>()//删除动态

    val networkState: LiveData<Int>
    val initialLoad: LiveData<Int>

    var isCreateOver: LiveData<Boolean>//判断是否网络请求参数完成
    var isKnowledge: Boolean = false//判断知识库的结果的有无


    private val factory: SearchQuestionDataSource.Factory

    init {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(3)
            .setPageSize(6)
            .setInitialLoadSizeHint(6)
            .build()
        factory = SearchQuestionDataSource.Factory(searchKey)
        questionList = LivePagedListBuilder<Int, Dynamic>(factory, config).build()
        networkState =
            Transformations.switchMap(factory.searchQuestionDataSourceLiveData) { it.networkState }
        initialLoad =
            Transformations.switchMap(factory.searchQuestionDataSourceLiveData) { it.initialLoad }
        isCreateOver =
            Transformations.switchMap(factory.searchQuestionDataSourceLiveData) { it.isCreateOver }
    }

    fun retry() = factory.searchQuestionDataSourceLiveData.value?.retry()
    fun invalidateSearchQuestionList() = questionList.value?.dataSource?.invalidate()

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

    fun ignore(dynamic: Dynamic) {
        LogUtils.d("RatleighZ", "when ignore, uid = ${dynamic.uid}")
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .ignoreUid(dynamic.uid)
            .setSchedulers()
            .doOnError {
                toastEvent.value = R.string.qa_ignore_dynamic_failure
                ignorePeople.value = false
            }
            .safeSubscribeBy {
                if (it.status == 200) {
                    toastEvent.value = R.string.qa_ignore_dynamic_success
                    ignorePeople.value = true
                }
            }

    }


    fun report(dynamic: Dynamic, content: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .report(dynamic.postId, CommentConfig.REPORT_DYNAMIC_MODEL, content)
            .setSchedulers()
            .doOnError {
                toastEvent.value = R.string.qa_report_dynamic_failure
            }
            .safeSubscribeBy {
                if (it.status == 200)
                    toastEvent.value = R.string.qa_report_dynamic_success
            }
    }

    fun deleteId(id: String, model: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .deleteId(id, model)
            .setSchedulers()
            .doOnError {
                toastEvent.value = R.string.qa_delete_dynamic_failure
                deleteTips.value = false
            }
            .safeSubscribeBy {
                deleteTips.value = true
            }
    }

    fun getKnowledge(key: String, page: Int = 1, size: Int = 6) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .getSearchKnowledge(key, page, size)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy {
                if (it.isNotEmpty()) {
                    LogUtils.d("zt", "5")
                    isKnowledge = true
                    knowledge.value = it
                } else {
                    LogUtils.d("zt", "6")
                    isKnowledge = false
                }
            }
    }
}