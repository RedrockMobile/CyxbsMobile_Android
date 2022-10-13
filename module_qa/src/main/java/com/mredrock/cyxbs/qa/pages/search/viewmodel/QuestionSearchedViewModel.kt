package com.mredrock.cyxbs.qa.pages.search.viewmodel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.Knowledge
import com.mredrock.cyxbs.qa.beannew.UserBrief
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.search.model.SearchQuestionDataSource


/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class QuestionSearchedViewModel(var searchKey: String) : BaseViewModel() {

    val questionList: LiveData<PagedList<Dynamic>>
    val knowledge = MutableLiveData<List<Knowledge>>()
    val userList = MutableLiveData<List<UserBrief>>()

    val ignorePeople = MutableLiveData<Boolean>()//屏蔽
    val deleteTips = MutableLiveData<Boolean>()//删除动态

    val networkState: LiveData<Int>
    val initialLoad: LiveData<Int>
    val userNetworkState = MutableLiveData<Int>()

    val isCreateOver: LiveData<Boolean>//判断是否网络请求参数完成
    var isKnowledge: Boolean = false//判断知识库的结果的有无


    private var factory: SearchQuestionDataSource.Factory

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

    fun getQuestions(key: String){
        searchKey = key
        //刷新factory里面的key值
        factory.refreshKey(key)
        //声明dataSource为过时，这样就会重新获取对应的dataSource进行分页加载
        factory.searchQuestionDataSourceLiveData.value?.invalidate()
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
            .reportDynamic(dynamic.postId,content)
            .setSchedulers()
            .doOnError {
                toastEvent.value = R.string.qa_report_dynamic_failure
            }
            .safeSubscribeBy {
                if (it.status == 200)
                    toastEvent.value = R.string.qa_report_dynamic_success
            }
    }

    fun deleteDynamic(id: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .deleteDynamic(id)
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
                    isKnowledge = true
                    knowledge.value = it
                } else {
                    isKnowledge = false
                    knowledge.value = listOf()
                }
            }
    }

    fun getUsers(key: String){
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .getSearchUsers(key)
            .mapOrThrowApiException()
            .setSchedulers()
            .doOnSubscribe { userNetworkState.value = NetworkState.LOADING }
            .doOnError {
                toastEvent.value = R.string.qa_search_no_user
                userNetworkState.value = NetworkState.FAILED
            }
            .safeSubscribeBy {
                userList.value = it
                userNetworkState.value = NetworkState.SUCCESSFUL
            }
    }

    fun changeFocusStatus(redid: String){
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .changeFocusStatus(redid)
            .setSchedulers()
            .doOnError {
                toastEvent.value = R.string.qa_person_change_focus_status_failed
            }
            .safeSubscribeBy {

            }
    }
}