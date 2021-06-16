package com.mredrock.cyxbs.qa.pages.mine.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiIllegalStateException
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.qa.beannew.Ignore
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.network.NetworkState

/**
 * Author: RayleighZ
 * Time: 2021-03-12 12:15
 */
class MyIgnoreDataSource : PageKeyedDataSource<Int,Ignore>() {

    val networkState = MutableLiveData<Int>()
    val initialLoad = MutableLiveData<Int>()
    var failedRequest: (() -> Unit)? = null

    class Factory : DataSource.Factory<Int, Ignore>() {
        val ignoreDataSourceLiveData = MutableLiveData<MyIgnoreDataSource>()

        override fun create(): MyIgnoreDataSource {
            val myIgnoreDataSource = MyIgnoreDataSource()
            ignoreDataSourceLiveData.postValue(myIgnoreDataSource)
            return myIgnoreDataSource
        }
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Ignore>) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getIgnoreUid(1, 6)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnSubscribe { networkState.postValue(NetworkState.LOADING) }
                .doOnError {
                    if (it is RedrockApiIllegalStateException){
                        networkState.postValue(NetworkState.NO_MORE_DATA)
                        initialLoad.postValue(NetworkState.SUCCESSFUL)
                    } else {
                        networkState.postValue(NetworkState.FAILED)
                        failedRequest = { loadInitial(params, callback) }
                    }
                }
                .safeSubscribeBy { list ->
                    initialLoad.postValue(NetworkState.SUCCESSFUL)
                    networkState.postValue(NetworkState.SUCCESSFUL)
                    val nextPageKey = 2.takeUnless { (list.size < params.requestedLoadSize) }
                    callback.onResult(list, 1, nextPageKey)
                }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Ignore>) = Unit

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Ignore>) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getIgnoreUid(params.key, 6)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnSubscribe { networkState.postValue(NetworkState.LOADING) }
                .doOnError {
                    if (it is RedrockApiIllegalStateException){
                        networkState.postValue(NetworkState.NO_MORE_DATA)
                        initialLoad.postValue(NetworkState.SUCCESSFUL)
                    } else {
                        networkState.postValue(NetworkState.FAILED)
                        failedRequest = { loadAfter(params, callback) }
                    }
                }
                .safeSubscribeBy { list ->
                    initialLoad.postValue(NetworkState.SUCCESSFUL)
                    networkState.postValue(NetworkState.SUCCESSFUL)
                    val adjacentPageKet = (params.key + 1).takeUnless { (list.size < params.requestedLoadSize) }
                    callback.onResult(list, adjacentPageKet)
                }
    }

    fun retry() = failedRequest?.invoke()
}