package com.mredrock.cyxbs.qa.pages.dynamic.viewmodel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.checkError
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.qa.bean.TestData
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.Topic
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.model.DynamicDataSource
import retrofit2.http.Field

/**
 * Created By jay68 on 2018/8/26.
 */
open class DynamicListViewModel(kind: String) : BaseViewModel() {
    val dynamicList: LiveData<PagedList<Dynamic>>
    val networkState: LiveData<Int>
    val initialLoad: LiveData<Int>
    var hotWords = MutableLiveData<List<String>>()
    var myCircle = MutableLiveData<List<Topic>>()
    private val factory: DynamicDataSource.Factory
    private var praiseNetworkState = NetworkState.SUCCESSFUL
    val refreshPreActivityEvent = SingleLiveEvent<Int>()

    //防止点赞快速点击
    var isDealing = false

    init {
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(3)
                .setPageSize(6)
                .setInitialLoadSizeHint(6)
                .build()
        factory = DynamicDataSource.Factory(kind)
        dynamicList = LivePagedListBuilder<Int, Dynamic>(factory, config).build()
        networkState = Transformations.switchMap(factory.dynamicDataSourceLiveData) { it.networkState }
        initialLoad = Transformations.switchMap(factory.dynamicDataSourceLiveData) { it.initialLoad }
    }

    fun getMyCirCleData() {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getFollowedTopic()
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    myCircle.value = it
                }
    }



    fun clickPraiseButton(position: Int, dynamic: Dynamic) {
        fun Boolean.toInt() = 1.takeIf { this@toInt } ?: -1

        if (praiseNetworkState == NetworkState.LOADING) {
//            toastEvent.value =
            return
        }
        ApiGenerator.getApiService(ApiService::class.java)
                .run {
                    if (dynamic.isPraised) {
                        cancelPraiseAnswer(dynamic.postId)
                    } else {
                        praiseAnswer(dynamic.postId)
                    }
                }
                .doOnSubscribe { isDealing = true }
                .checkError()
                .setSchedulers()
                .doOnError {
//                    toastEvent.value = R.string.qa_service_error_hint
                    isDealing = false
                }
                .doFinally {
                    praiseNetworkState = NetworkState.SUCCESSFUL
                }
                .safeSubscribeBy {
                    isDealing = false
                    dynamic.apply {
                        val state = !isPraised
                        praiseCount = dynamic.praiseCount + state.toInt()
                        isPraised = state
                    }
                    refreshPreActivityEvent.value = position
                }
                .lifeCycle()
    }

    fun getScrollerText() {
        ApiGenerator.getApiService(ApiService::class.java)
                .getHotWords()
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy { texts ->
                    hotWords.value = texts.scrollerHotWord
                }
    }

    fun invalidateQuestionList() = dynamicList.value?.dataSource?.invalidate()

    fun retry() = factory.dynamicDataSourceLiveData.value?.retry()

    class Factory(private val kind: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(DynamicListViewModel::class.java)) {
                return DynamicListViewModel(kind) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}