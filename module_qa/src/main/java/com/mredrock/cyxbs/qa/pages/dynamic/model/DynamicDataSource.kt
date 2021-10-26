package com.mredrock.cyxbs.qa.pages.dynamic.model

import android.util.Log
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

/**
 * Created By jay68 on 2018/9/20.
 */
class DynamicDataSource(private val kind: String,private val redid: String?) : PageKeyedDataSource<Int, Dynamic>() {
    val networkState = MutableLiveData<Int>()
    val initialLoad = MutableLiveData<Int>()

    private var failedRequest: (() -> Unit)? = null


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Dynamic>
    ) {
        //最开始加上判断，以防登录bug
        val userState = ServiceManager.getService(IAccountService::class.java).getVerifyService()
        if (!userState.isLogin() && !userState.isTouristMode()) {
            callback.onResult(listOf(), 1, null)
            initialLoad.postValue(NetworkState.CANNOT_LOAD_WITHOUT_LOGIN)
            return
        }
        Log.e("wxtag跨模块刷新","(DynamicDataSource.kt:59)->> loadInitial kind=$kind")
        //此处是接口设计时没有考虑太好，个人dynamic接口与其他的dynamic接口分开了
        //故需要做一次判断，如果将来有可能可以合并两个接口以减少冗余
        when(kind){
            "mine" -> {
                ApiGenerator.getApiService(ApiServiceNew::class.java)
                    .getUserDynamic(1, params.requestedLoadSize)
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

            "personal" -> {
              Log.e("wxtag跨模块刷新","(DynamicDataSource.kt:59)->> 执行正确")
                ApiGenerator.getApiService(ApiServiceNew::class.java)
                    .getPersoalDynamic(redid, 1,6)
                    .mapOrThrowApiException()
                    .setSchedulers()
                    .doOnSubscribe { initialLoad.postValue(NetworkState.LOADING) }
                    .doOnError {
                        Log.e("wxtag跨模块刷新","(DynamicDataSource.kt:59)->> 出错了$it")
                        initialLoad.value = NetworkState.FAILED
                        failedRequest = { loadInitial(params, callback) }
                    }
                    .safeSubscribeBy { list ->
                        Log.e("wxtag跨模块刷新","(DynamicDataSource.kt:59)->> 执行正确list=$list")
                        initialLoad.value = NetworkState.SUCCESSFUL
                        val nextPageKey = 2.takeUnless { (list.size < params.requestedLoadSize) }
                        callback.onResult(list, 1, nextPageKey)
                    }
            }

            "main" -> {
                ApiGenerator.getApiService(ApiServiceNew::class.java)
                    .getDynamicList(kind, 1, params.requestedLoadSize)
                    .mapOrThrowApiException()
                    .setSchedulers()
                    .doOnSubscribe { initialLoad.postValue(NetworkState.LOADING) }
                    .doOnError {
                        Log.d("TAG","(DynamicDataSource.kt:65)->$it")
                        initialLoad.value = NetworkState.FAILED
                        failedRequest = { loadInitial(params, callback) }
                    }
                    .safeSubscribeBy { list ->
                        initialLoad.value = NetworkState.SUCCESSFUL
                        val nextPageKey = 2.takeUnless { (list.size < params.requestedLoadSize) }
                        callback.onResult(list, 1, nextPageKey)
                    }
            }

            else -> {
                ApiGenerator.getApiService(ApiServiceNew::class.java)
                    .getFocusDynamicList(1, params.requestedLoadSize)
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
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Dynamic>) {
        Log.e("wxtag跨模块刷新","(DynamicDataSource.kt:59)->> loadAfter kind=$kind")
        when (kind) {
            "mine" -> {
                ApiGenerator.getApiService(ApiServiceNew::class.java)
                    .getUserDynamic(params.key, params.requestedLoadSize)
                    .mapOrThrowApiException()
                    .setSchedulers()
                    .doOnSubscribe { networkState.postValue(NetworkState.LOADING) }
                    .doOnError {
                        networkState.value = NetworkState.FAILED
                        failedRequest = { loadAfter(params, callback) }
                    }
                    .safeSubscribeBy { list ->
                        networkState.value = NetworkState.SUCCESSFUL
                        val adjacentPageKey =
                            (params.key + 1).takeUnless { list.size < params.requestedLoadSize }
                        callback.onResult(list, adjacentPageKey)
                    }
            }
            "personal" -> {
                Log.e("wxtag跨模块刷新","(DynamicDataSource.kt:59)->> 执行正确")
                ApiGenerator.getApiService(ApiServiceNew::class.java)
                    .getPersoalDynamic(redid, params.key, params.requestedLoadSize)
                    .mapOrThrowApiException()
                    .setSchedulers()
                    .doOnSubscribe { networkState.postValue(NetworkState.LOADING) }
                    .doOnError {
                        networkState.value = NetworkState.FAILED
                        failedRequest = { loadAfter(params, callback) }
                    }
                    .safeSubscribeBy { list ->
                        networkState.value = NetworkState.SUCCESSFUL
                        val adjacentPageKey =
                            (params.key + 1).takeUnless { list.size < params.requestedLoadSize }
                        callback.onResult(list, adjacentPageKey)
                    }
            }
            "main" -> {
                ApiGenerator.getApiService(ApiServiceNew::class.java)
                    .getDynamicList(kind, 1, params.requestedLoadSize)
                    .mapOrThrowApiException()
                    .setSchedulers()
                    .doOnSubscribe { networkState.postValue(NetworkState.LOADING) }
                    .doOnError {
                        networkState.value = NetworkState.FAILED
                        failedRequest = { loadAfter(params, callback) }
                    }
                    .safeSubscribeBy { list ->
                        networkState.value = NetworkState.SUCCESSFUL
                        val adjacentPageKey =
                            (params.key + 1).takeUnless { list.size < params.requestedLoadSize }
                        callback.onResult(list, adjacentPageKey)
                    }
            }

            else -> {
                ApiGenerator.getApiService(ApiServiceNew::class.java)
                    .getFocusDynamicList(1, params.requestedLoadSize)
                    .mapOrThrowApiException()
                    .setSchedulers()
                    .doOnSubscribe { networkState.postValue(NetworkState.LOADING) }
                    .doOnError {
                        networkState.value = NetworkState.FAILED
                        failedRequest = { loadAfter(params, callback) }
                    }
                    .safeSubscribeBy { list ->
                        networkState.value = NetworkState.SUCCESSFUL
                        val adjacentPageKey =
                            (params.key + 1).takeUnless { list.size < params.requestedLoadSize }
                        callback.onResult(list, adjacentPageKey)
                    }
            }
        }
    }

    fun retry() {
        failedRequest?.invoke()
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Dynamic>) = Unit

    class Factory(private val kind: String,val redid:String?=null) : DataSource.Factory<Int, Dynamic>() {
        val dynamicDataSourceLiveData = MutableLiveData<DynamicDataSource>()

        override fun create(): DynamicDataSource {
            Log.e("wxtag跨模块刷新","(MyDynamicFragment.kt:177)->> create():redid=$redid 还有kind=$kind")
            val dynamicDataSource = DynamicDataSource(kind,redid)
            dynamicDataSourceLiveData.postValue(dynamicDataSource)
            return dynamicDataSource
        }
    }
}