package com.mredrock.cyxbs.qa.pages.search.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.utils.isNullOrEmpty

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class SearchQuestionDataSource(private val kind: String) : PageKeyedDataSource<Int, Dynamic>() {

    val networkState = MutableLiveData<Int>()
    val initialLoad = MutableLiveData<Int>()
    val isCreateOver = MutableLiveData<Boolean>()
    private var failedRequest: (() -> Unit)? = null

    companion object {
        var SEARCH_RESULT = false//暴露给外面是否有数据的变量
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Dynamic>
    ) {
        //最开始加上判断，以防登录bug
        val userState = ServiceManager(IAccountService::class).getVerifyService()
        if (!userState.isLogin() && !userState.isTouristMode()) {
            callback.onResult(listOf(), 1, null)
            initialLoad.postValue(NetworkState.CANNOT_LOAD_WITHOUT_LOGIN)
            return
        }
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .getSearchResult(kind, 1, params.requestedLoadSize)
            .mapOrThrowApiException()
            .setSchedulers()
            .doOnSubscribe {
                initialLoad.postValue(NetworkState.LOADING)
            }
            .doOnError {
                initialLoad.value = NetworkState.FAILED
                failedRequest = { loadInitial(params, callback) }
            }
            .unsafeSubscribeBy { list ->
                initialLoad.value = NetworkState.SUCCESSFUL
                SEARCH_RESULT = !list.isNullOrEmpty()
                val nextPageKey = 2.takeUnless { (list.size < params.requestedLoadSize) }
                callback.onResult(list, 1, nextPageKey)
                isCreateOver.value = true
            }

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Dynamic>) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .getSearchResult(kind, params.key, params.requestedLoadSize)
            .mapOrThrowApiException()
            .setSchedulers()
            .doOnSubscribe { networkState.postValue(NetworkState.LOADING) }
            .doOnError {
                networkState.value = NetworkState.FAILED
                failedRequest = { loadAfter(params, callback) }
            }
            .unsafeSubscribeBy { list ->
                networkState.value = NetworkState.SUCCESSFUL
                SEARCH_RESULT = !list.isNullOrEmpty()
                val adjacentPageKey =
                    (params.key + 1).takeUnless { list.size < params.requestedLoadSize }
                callback.onResult(list, adjacentPageKey)
                isCreateOver.value = true
            }
    }

    fun retry() {
        failedRequest?.invoke()
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Dynamic>) = Unit

    class Factory(private var key:String) : DataSource.Factory<Int, Dynamic>() {
        val searchQuestionDataSourceLiveData = MutableLiveData<SearchQuestionDataSource>()

        fun refreshKey(newKey:String){
            key = newKey
        }

        override fun create(): SearchQuestionDataSource {
            val searchQuestionDataSource = SearchQuestionDataSource(key)
            searchQuestionDataSourceLiveData.postValue(searchQuestionDataSource)
            return searchQuestionDataSource
        }
    }
}