package com.mredrock.cyxbs.qa.pages.mine.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.qa.beannew.CommentWrapper
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.network.NetworkState

/**
 * @date 2021-03-06
 * @author Sca RayleighZ
 */
class MyCommentDataSource: PageKeyedDataSource<Int, CommentWrapper>() {
    val networkState = MutableLiveData<Int>()
    val initialLoad = MutableLiveData<Int>()
    private var failedRequest: (() -> Unit)? = null
    private val cwList = ArrayList<CommentWrapper>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, CommentWrapper>) {
        //最开始加上判断，以防登录bug
        val userState = ServiceManager.getService(IAccountService::class.java).getVerifyService()
        if (!userState.isLogin() && !userState.isTouristMode()) {
            callback.onResult(listOf(), 1, null)
            initialLoad.postValue(NetworkState.CANNOT_LOAD_WITHOUT_LOGIN)
            return
        }

        cwList.clear()
        //由于数据来自不同的两个接口，所以这里需要进行两次加载
        var type1IsOver = false
        var type2IsOver = false

        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getUserReplay(
                        1,
                        params.requestedLoadSize / 2,
                        1
                )
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnSubscribe { networkState.postValue(NetworkState.LOADING) }
                .doOnError {
                    networkState.value = NetworkState.FAILED
                    LogUtils.d("RayleighZ", it.toString())
                    failedRequest = { loadInitial(params, callback) }
                }
                .safeSubscribeBy { list ->
                    initialLoad.value = NetworkState.SUCCESSFUL
                    val nextPageKey = 2.takeUnless { (list.size < params.requestedLoadSize) }
                    cwList.addAll(list)
//                    LogUtils.d("RayleighZ", "onSuccess, need to success = ${type1IsOver && type2IsOver}")
                    LogUtils.d("RayleighZ", "onSuccess size = ${list.size}")
                    type1IsOver = true
                    callback.onResult(list, 1, nextPageKey)
//                    if (type1IsOver && type2IsOver) {
//                        callback.onResult(list, 1, nextPageKey)
//                    }
                }

        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getUserReplay(
                        1,
                        params.requestedLoadSize / 2,
                        2
                )
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnSubscribe { networkState.postValue(NetworkState.LOADING) }
                .doOnError {
                    networkState.value = NetworkState.FAILED
                    LogUtils.d("RayleighZ", it.toString())
                    failedRequest = { loadInitial(params, callback) }
                }
                .safeSubscribeBy { list ->
                    initialLoad.value = NetworkState.SUCCESSFUL
                    val nextPageKey = 2.takeUnless { (list.size < params.requestedLoadSize) }
                    cwList.addAll(list)
                    LogUtils.d("RayleighZ", "onSuccess size = ${list.size}")
//                    LogUtils.d("RayleighZ", "onSuccess, need to success = ${type1IsOver && type2IsOver}")
                    type2IsOver = true
                    callback.onResult(list, 1, nextPageKey)
//                    if (type2IsOver && type2IsOver) {
//                        callback.onResult(list, 1, nextPageKey)
//                    }
                }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, CommentWrapper>) = Unit

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, CommentWrapper>) {

        //由于数据来自不同的两个接口，所以这里需要进行两次加载
        var type1IsOver = false
        var type2IsOver = false
        cwList.clear()

        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getUserReplay(
                        1,
                        params.requestedLoadSize / 2,
                        1
                )
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnSubscribe { networkState.postValue(NetworkState.LOADING) }
                .doOnError {
                    networkState.value = NetworkState.FAILED
                    LogUtils.d("RayleighZ", it.toString())
                    failedRequest = { loadAfter(params, callback) }
                }
                .safeSubscribeBy { list ->
                    networkState.value = NetworkState.SUCCESSFUL
                    val adjacentPageKey = (params.key + 1).takeUnless { list.size < params.requestedLoadSize }
                    callback.onResult(list, adjacentPageKey)
                    type1IsOver = true
                    LogUtils.d("RayleighZ", "onSuccess, need to success = ${type1IsOver && type2IsOver}")
                    callback.onResult(list, adjacentPageKey)
//                    if (type1IsOver && type2IsOver){
//                        callback.onResult(list, adjacentPageKey)
//                    }
                }

        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getUserReplay(
                        1,
                        params.requestedLoadSize / 2,
                        2
                )
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnSubscribe { networkState.postValue(NetworkState.LOADING) }
                .doOnError {
                    networkState.value = NetworkState.FAILED
                    LogUtils.d("RayleighZ", it.toString())
                    failedRequest = { loadAfter(params, callback) }
                }
                .safeSubscribeBy { list ->
                    networkState.value = NetworkState.SUCCESSFUL
                    val adjacentPageKey = (params.key + 1).takeUnless { list.size < params.requestedLoadSize }
                    type2IsOver = true
                    LogUtils.d("RayleighZ", "onSuccess, need to success = ${type1IsOver && type2IsOver}")
                    callback.onResult(list, adjacentPageKey)
//                    if (type1IsOver && type2IsOver){
//                        callback.onResult(list, adjacentPageKey)
//                    }
                }
    }

    fun retry() = failedRequest?.invoke()

    class Factory: DataSource.Factory<Int, CommentWrapper>() {
        val cwListDataSource = MutableLiveData<MyCommentDataSource>()

        override fun create(): MyCommentDataSource {
            val myCommentDataSource = MyCommentDataSource()
            cwListDataSource.postValue(myCommentDataSource)
            return myCommentDataSource
        }
    }

}