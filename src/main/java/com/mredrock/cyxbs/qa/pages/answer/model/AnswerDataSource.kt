package com.mredrock.cyxbs.qa.pages.answer.model

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import android.arch.paging.PageKeyedDataSource
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.network.NetworkState

/**
 * Created By jay68 on 2018/10/6.
 */
class AnswerDataSource(private val qid: String) : PageKeyedDataSource<Int, Answer>() {
    val networkState = MutableLiveData<Int>()
    val initialLoad = MutableLiveData<Int>()

    private var failedRequest: (() -> Unit)? = null

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Answer>) {
        val user = BaseApp.user!!
        ApiGenerator.getApiService(ApiService::class.java)
                .getAnswerList(qid, 1, params.requestedLoadSize, user.stuNum!!, user.idNum!!)
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

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Answer>) {
        val user = BaseApp.user!!
        ApiGenerator.getApiService(ApiService::class.java)
                .getAnswerList(qid, params.key, params.requestedLoadSize, user.stuNum!!, user.idNum!!)
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

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Answer>) = Unit

    class Factory(private val qid: String) : DataSource.Factory<Int, Answer>() {
        val answerDataSourceLiveData = MutableLiveData<AnswerDataSource>()

        override fun create(): AnswerDataSource {
            val answerDataSource = AnswerDataSource(qid)
            answerDataSourceLiveData.postValue(answerDataSource)
            return answerDataSource
        }
    }
}