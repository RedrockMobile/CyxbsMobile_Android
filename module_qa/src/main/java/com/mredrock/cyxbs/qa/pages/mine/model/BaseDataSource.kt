package com.mredrock.cyxbs.qa.pages.mine.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.qa.network.NetworkState

/**
 * Author: RayleighZ
 * Time: 2021-03-12 12:15
 */
open class BaseDataSource<T> : PageKeyedDataSource<Int, T>() {

    val networkState = MutableLiveData<Int>()
    val initialLoad = MutableLiveData<Int>()
    var failedRequest: (() -> Unit)? = null
    var requestType = 1
    var needAddKey = false
    var needStop = false
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {
        //最开始加上判断，以防登录bug
        val userState = ServiceManager.getService(IAccountService::class.java).getVerifyService()
        if (!userState.isLogin() && !userState.isTouristMode()) {
            callback.onResult(listOf(), 1, null)
            initialLoad.postValue(NetworkState.CANNOT_LOAD_WITHOUT_LOGIN)
            return
        }
        sendInitRequest(params, callback)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) = Unit

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        sendLoadRequest(params, callback)
    }

    fun retry() = failedRequest?.invoke()

    open fun sendInitRequest(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>){ }

    open fun sendLoadRequest(params: LoadParams<Int>, callback: LoadCallback<Int, T>){ }

}