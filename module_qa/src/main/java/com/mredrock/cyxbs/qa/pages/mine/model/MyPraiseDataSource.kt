package com.mredrock.cyxbs.qa.pages.mine.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiIllegalStateException
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.qa.beannew.Praise
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.network.NetworkState

/**
 * Author: RayleighZ
 * Time: 2021-03-11 1:17
 */
class MyPraiseDataSource : PageKeyedDataSource<Int, Praise>() {

    val networkState = MutableLiveData<Int>()
    val initialLoad = MutableLiveData<Int>()
    private var failedRequest: (() -> Unit)? = null

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Praise>) {
        //最开始加上判断，以防登录bug
        val userState = ServiceManager.getService(IAccountService::class.java).getVerifyService()
        if (!userState.isLogin() && !userState.isTouristMode()) {
            callback.onResult(listOf(), 1, null)
            initialLoad.postValue(NetworkState.CANNOT_LOAD_WITHOUT_LOGIN)
            return
        }

        //由于接口的type1和type2的page和size并不相关，所以这里的请求逻辑为
        //1：固定size为6
        //2：奇数次加载为type1，偶数次加载为type2

        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getUserPraise(
                        1,
                        params.requestedLoadSize
                )
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
                    networkState.postValue(NetworkState.SUCCESSFUL)
                    initialLoad.postValue(NetworkState.SUCCESSFUL)
                    val nextKey = 2.takeUnless { list.size < params.requestedLoadSize }
                    callback.onResult(list, 1, nextKey)
                }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Praise>) = Unit

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Praise>) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getUserPraise(
                        params.key,
                        params.requestedLoadSize
                )
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
                    networkState.postValue(NetworkState.SUCCESSFUL)
                    initialLoad.postValue(NetworkState.SUCCESSFUL)
                    val adjacentPageKey = (params.key+1).takeUnless { list.size < params.requestedLoadSize }
                    callback.onResult(list, adjacentPageKey)
                }
    }

    fun retry() = failedRequest?.invoke()

    class Factory : DataSource.Factory<Int, Praise>() {
        val praiseDataSourceLiveData = MutableLiveData<MyPraiseDataSource>()

        override fun create(): MyPraiseDataSource {
            val myPraiseDataSource = MyPraiseDataSource()
            praiseDataSourceLiveData.postValue(myPraiseDataSource)
            return myPraiseDataSource
        }
    }
}