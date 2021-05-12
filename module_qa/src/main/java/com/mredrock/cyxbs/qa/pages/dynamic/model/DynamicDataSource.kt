package com.mredrock.cyxbs.qa.pages.dynamic.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.network.NetworkState

/**
 * Created By jay68 on 2018/9/20.
 */
class DynamicDataSource(private val kind: String) : PageKeyedDataSource<Int, Dynamic>() {
    val networkState = MutableLiveData<Int>()
    val initialLoad = MutableLiveData<Int>()

    private var failedRequest: (() -> Unit)? = null


    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Dynamic>) {
        //最开始加上判断，以防登录bug
        val userState = ServiceManager.getService(IAccountService::class.java).getVerifyService()
        if (!userState.isLogin() && !userState.isTouristMode()) {
            callback.onResult(listOf(), 1, null)
            initialLoad.postValue(NetworkState.CANNOT_LOAD_WITHOUT_LOGIN)
            return
        }
        //此处是接口设计时没有考虑太好，个人dynamic接口与其他的dynamic接口分开了
        //故需要做一次判断，如果将来有可能可以合并两个接口以减少冗余
        if (kind == "mine"){
            ApiGenerator.getApiService(ApiServiceNew::class.java)
                    .getUserDynamic(1, params.requestedLoadSize)
                    .mapOrThrowApiException()
                    .setSchedulers()
                    .doOnSubscribe { initialLoad.postValue(NetworkState.LOADING) }
                    .doOnError {
                        initialLoad.value = NetworkState.FAILED
                        failedRequest = { loadInitial(params, callback) }
                    }
                    .safeSubscribeBy {
                        list ->
                        initialLoad.value = NetworkState.SUCCESSFUL
                        val nextPageKey = 2.takeUnless { (list.size < params.requestedLoadSize) }
                        callback.onResult(list, 1, nextPageKey)
                    }
            return
        }
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getDynamicList(kind, 1, params.requestedLoadSize)
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

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Dynamic>) {
        if (kind == "mine"){
            ApiGenerator.getApiService(ApiServiceNew::class.java)
                    .getUserDynamic(params.key, params.requestedLoadSize)
                    .mapOrThrowApiException()
                    .setSchedulers()
                    .doOnSubscribe { initialLoad.postValue(NetworkState.LOADING) }
                    .doOnError {
                        initialLoad.value = NetworkState.FAILED
                        failedRequest = { loadAfter(params, callback) }
                    }
                    .safeSubscribeBy {
                        list ->
                        initialLoad.value = NetworkState.SUCCESSFUL
                        val adjacentPageKey = (params.key + 1).takeUnless { list.size < params.requestedLoadSize }
                        callback.onResult(list, adjacentPageKey)
                    }
            return
        }
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getDynamicList(kind, params.key, params.requestedLoadSize)
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

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Dynamic>) = Unit

    class Factory(private val kind: String) : DataSource.Factory<Int, Dynamic>() {
        val dynamicDataSourceLiveData = MutableLiveData<DynamicDataSource>()

        override fun create(): DynamicDataSource {
            val dynamicDataSource = DynamicDataSource(kind)
            dynamicDataSourceLiveData.postValue(dynamicDataSource)
            return dynamicDataSource
        }
    }
}