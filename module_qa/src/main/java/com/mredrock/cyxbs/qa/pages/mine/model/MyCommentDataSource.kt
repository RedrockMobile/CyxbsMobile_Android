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
import com.mredrock.cyxbs.qa.beannew.CommentWrapper
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.network.NetworkState

/**
 * @date 2021-03-06
 * @author Sca RayleighZ
 */
class MyCommentDataSource : PageKeyedDataSource<Int, CommentWrapper>() {
    val networkState = MutableLiveData<Int>()
    val initialLoad = MutableLiveData<Int>()
    private var failedRequest: (() -> Unit)? = null
    private var requestType = 1
    private var needAddKey = false
    private var needStop = false

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, CommentWrapper>) {
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
        //由于存在请求某一个人type返回值为空的情况，所以需要单独进行判空处理

        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getUserReplay(
                        1,
                        params.requestedLoadSize,
                        requestType
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
                    initialLoad.postValue(NetworkState.SUCCESSFUL)
                    networkState.postValue(NetworkState.SUCCESSFUL)
                    if (list.isNotEmpty()){//如果这个集合是空的，不予以返回，因为这里还不能确定后面是否为空
                        callback.onResult(list, 1, 1)
                    }
                }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, CommentWrapper>) = Unit

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, CommentWrapper>) {
        requestType = if (requestType == 1) 2 else 1
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getUserReplay(
                        params.key,
                        params.requestedLoadSize,
                        requestType
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
                    needAddKey = !needAddKey
                    networkState.postValue(NetworkState.SUCCESSFUL)
                    initialLoad.postValue(NetworkState.SUCCESSFUL)
                    val adjacentPageKey = if (needAddKey) params.key + 1 else params.key
                    if (list.size < params.requestedLoadSize) {
                        //adjacentPageKey为null是加载的结束标志
                        //只有两个type下的size均小于requestLoadSize才算正式结束
                        if (needStop) {
                            callback.onResult(list, null)
                        } else {
                            needStop = true
                            callback.onResult(list, adjacentPageKey)
                        }
                        return@safeSubscribeBy
                    }
                    callback.onResult(list, adjacentPageKey)

                }

    }

    fun retry() = failedRequest?.invoke()

    class Factory : DataSource.Factory<Int, CommentWrapper>() {
        val cwListDataSource = MutableLiveData<MyCommentDataSource>()

        override fun create(): MyCommentDataSource {
            val myCommentDataSource = MyCommentDataSource()
            cwListDataSource.postValue(myCommentDataSource)
            return myCommentDataSource
        }
    }

}