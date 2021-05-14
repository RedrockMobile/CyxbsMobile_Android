package com.mredrock.cyxbs.qa.pages.square.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.model.DynamicDataSource

/**
 *@Date 2021-02-27
 *@Time 17:06
 *@author SpreadWater
 *@description 圈子获取最新和热门的datasource
 */
class CircleDynamicDataSource(private val kind: String, private val loop: Int) : PageKeyedDataSource<Int, Dynamic>() {
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
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getCircleDynamicList(loop, 1, params.requestedLoadSize, kind)
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
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getCircleDynamicList(loop, params.key, params.requestedLoadSize, kind)
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

    class Factory(private val kind: String, private val loop: Int) : DataSource.Factory<Int, Dynamic>() {
        val circleDynamicDataSourceLiveData = MutableLiveData<CircleDynamicDataSource>()

        override fun create(): CircleDynamicDataSource {
            val circleDynamicDataSource = CircleDynamicDataSource(kind, loop)
            circleDynamicDataSourceLiveData.postValue(circleDynamicDataSource)
            return circleDynamicDataSource
        }
    }
}