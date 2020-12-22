package com.mredrock.cyxbs.qa.pages.dynamic.viewmodel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.Topic
import com.mredrock.cyxbs.qa.beannew.TopicMessage
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.pages.dynamic.model.DynamicDataSource
import com.mredrock.cyxbs.qa.pages.dynamic.model.TopicDataSet

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
    val ignorePeople = MutableLiveData<Boolean>()
    val deleteTips = MutableLiveData<Boolean>()
    val topicMessageList = MutableLiveData<List<TopicMessage>>()

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
                .doOnError {
                    toastEvent.value = R.string.qa_get_circle_data_failure
                }
                .safeSubscribeBy {
                    myCircle.value = it
                    it.forEach {
                        TopicDataSet.storageTopicData(it)
                    }
                }
    }

    fun getTopicMessages(timeStamp: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getTopicMessage(timeStamp)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnError {
                    toastEvent.value = R.string.qa_topic_message_failure
                }
                .safeSubscribeBy {
                    topicMessageList.value = it
                }
    }

    fun ignore(dynamic: Dynamic) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .ignoreUid(dynamic.postId)
                .setSchedulers()
                .doOnError {
                    toastEvent.value = R.string.qa_ignore_dynamic_failure
                    ignorePeople.value = false
                }
                .doOnSubscribe {
                    ignorePeople.value = true
                }
                .safeSubscribeBy {
                    if (it.status == 200) {
                        toastEvent.value = R.string.qa_ignore_dynamic_success
                    }
                }

    }


    fun report(dynamic: Dynamic, content: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .report(dynamic.postId, CommentConfig.REPORT_DYNAMIC_MODEL, content)
                .setSchedulers()
                .doOnError {
                    toastEvent.value = R.string.qa_report_dynamic_failure
                }
                .safeSubscribeBy {
                    if (it.status == 200)
                        toastEvent.value = R.string.qa_report_dynamic_success
                }
    }

    fun deleteId(id: String, model: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .deleteId(id, model)
                .setSchedulers()
                .doOnError {
                    toastEvent.value = R.string.qa_delete_dynamic_failure
                    deleteTips.value = false
                }
                .safeSubscribeBy {
                    deleteTips.value = true
                }
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