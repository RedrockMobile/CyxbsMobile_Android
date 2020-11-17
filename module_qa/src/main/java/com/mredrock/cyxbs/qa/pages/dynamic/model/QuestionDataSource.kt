package com.mredrock.cyxbs.qa.pages.dynamic.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.account.IAccountService
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.network.NetworkState

/**
 * Created By jay68 on 2018/9/20.
 */
class QuestionDataSource(private val kind: String) : PageKeyedDataSource<Int, Question>() {
    val networkState = MutableLiveData<Int>()
    val initialLoad = MutableLiveData<Int>()

    private var failedRequest: (() -> Unit)? = null

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Question>) {
        //最开始加上判断，以防登录bug
        val userState = ServiceManager.getService(IAccountService::class.java).getVerifyService()
        if (!userState.isLogin() && !userState.isTouristMode()) {
            callback.onResult(listOf(), 1, null)
            initialLoad.postValue(NetworkState.CANNOT_LOAD_WITHOUT_LOGIN)
            return
        }
        ApiGenerator.getApiService(ApiService::class.java)
                .getQuestionList(kind, 1, params.requestedLoadSize)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnSubscribe { initialLoad.postValue(NetworkState.LOADING) }
                .doOnError {
                    initialLoad.value = NetworkState.FAILED
                    failedRequest = { loadInitial(params, callback) }
                }
                .safeSubscribeBy { list ->
                    initialLoad.value = NetworkState.SUCCESSFUL
                    val nextPageKey = 2.takeUnless { (list.size < params.requestedLoadSize) }
                    callback.onResult(list, 1, nextPageKey)
                }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Question>) {
        ApiGenerator.getApiService(ApiService::class.java)
                .getQuestionList(kind, params.key, params.requestedLoadSize)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnSubscribe { networkState.postValue(NetworkState.LOADING) }
                .doOnError {
                    networkState.value = NetworkState.FAILED
                    failedRequest = { loadAfter(params, callback) }
                }
                .safeSubscribeBy { list ->
                    networkState.value = NetworkState.SUCCESSFUL
                    val adjacentPageKey = (params.key + 1).takeUnless { list.size < params.requestedLoadSize }
                    callback.onResult(list, adjacentPageKey)
                }
    }

    fun retry() {
        failedRequest?.invoke()
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Question>) = Unit

    class Factory(private val kind: String) : DataSource.Factory<Int, Question>() {
        val questionDataSourceLiveData = MutableLiveData<QuestionDataSource>()

        override fun create(): QuestionDataSource {
            val questionDataSource = QuestionDataSource(kind)
            questionDataSourceLiveData.postValue(questionDataSource)
            return questionDataSource
        }
    }
}