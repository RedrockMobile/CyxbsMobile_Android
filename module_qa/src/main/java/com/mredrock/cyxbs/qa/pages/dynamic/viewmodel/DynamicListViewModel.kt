package com.mredrock.cyxbs.qa.pages.dynamic.viewmodel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.bean.isSuccessful
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.*
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.pages.dynamic.model.DynamicDataSource
import com.mredrock.cyxbs.qa.pages.dynamic.model.TopicDataSet

/**
 * rebuild by xgl 2021/2/13
 */
open class DynamicListViewModel : BaseViewModel() {
    private val recommendFactory: DynamicDataSource.Factory
    private val focusFactory: DynamicDataSource.Factory

    val recommendList: LiveData<PagedList<Message>>
    val recommendNetworkState: LiveData<Int>
    val recommendInitialLoad: LiveData<Int>

    val focusList: LiveData<PagedList<Message>>
    val focusNetworkState: LiveData<Int>
    val focusInitialLoad: LiveData<Int>

    val ignorePeople = MutableLiveData<Boolean>()
    val deleteTips = MutableLiveData<Boolean>()

    var hotWords = MutableLiveData<List<String>>()
    var myCircle = MutableLiveData<List<Topic>>()
    val topicMessageList = MutableLiveData<List<TopicMessage>>()

    init {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(3)
            .setPageSize(6)
            .setInitialLoadSizeHint(6)
            .build()
        recommendFactory = DynamicDataSource.Factory("main")
        focusFactory = DynamicDataSource.Factory("focus")

        recommendList = LivePagedListBuilder(recommendFactory, config).build()
        focusList = LivePagedListBuilder(focusFactory, config).build()

        recommendNetworkState =
            Transformations.switchMap(recommendFactory.dynamicDataSourceLiveData) { it.networkState }
        recommendInitialLoad =
            Transformations.switchMap(recommendFactory.dynamicDataSourceLiveData) { it.initialLoad }

        focusNetworkState =
            Transformations.switchMap(focusFactory.dynamicDataSourceLiveData) { it.networkState }
        focusInitialLoad =
            Transformations.switchMap(focusFactory.dynamicDataSourceLiveData) { it.initialLoad }
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
            }
    }

    fun getAllCirCleData(topic_name: String, instruction: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .getTopicGround(topic_name, instruction)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy { it ->
                TopicDataSet.clearCircleDetailTime()
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

    fun getScrollerText() {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .getSearchHotWord()
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy { texts ->
                hotWords.value = texts.hotWords
            }
    }

    fun followGroup(topicName: String, followState: Boolean) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .followTopicGround(topicName)
            .setSchedulers()
            .doOnError {
                if (followState) {
                    toast("取消关注失败")
                } else {
                    toast("关注失败")
                }
            }
            .safeSubscribeBy {
                if (it.isSuccessful) {
                    if (followState) {
                        //如果处于关注状态,点击之后是取消关注
                        toastEvent.value = R.string.qa_unfollow_circle
                    } else {
                        //如果处于未关注状态,点击之后是关注
                        toastEvent.value = R.string.qa_follow_circle
                    }
                } else {
                    if (followState) {
                        toast("取消关注失败")
                    } else {
                        toast("关注失败")
                    }
                }
                //刷新数据
                getMyCirCleData()
                invalidateRecommendList()
                invalidateFocusList()
            }
    }

    fun ignore(dynamic: Dynamic) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .ignoreUid(dynamic.uid)
            .setSchedulers()
            .doOnError {
                toastEvent.value = R.string.qa_ignore_dynamic_failure
            }
            .safeSubscribeBy {
                if (it.status == 200) {
                    toastEvent.value = R.string.qa_ignore_dynamic_success
                    ignorePeople.value = true
                }
            }

    }

    fun report(dynamic: Dynamic, content: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .reportDynamic(dynamic.postId,content)
            .setSchedulers()
            .doOnError {
                toastEvent.value = R.string.qa_report_dynamic_failure
            }
            .safeSubscribeBy {
                if (it.status == 200)
                    toastEvent.value = R.string.qa_report_dynamic_success
            }
    }

    fun deleteDynamic(id: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .deleteDynamic(id)
            .setSchedulers()
            .doOnError {
                toastEvent.value = R.string.qa_delete_dynamic_failure
            }
            .safeSubscribeBy {
                deleteTips.value = true
                toastEvent.value = R.string.qa_delete_dynamic_success
            }
    }

    fun invalidateRecommendList() = recommendList.value?.dataSource?.invalidate()
    fun invalidateFocusList() = focusList.value?.dataSource?.invalidate()

    fun retryRecommend() = recommendFactory.dynamicDataSourceLiveData.value?.retry()
    fun retryFocus() = focusFactory.dynamicDataSourceLiveData.value?.retry()

//    class Factory(private val kind: String) : ViewModelProvider.Factory {
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//            @Suppress("UNCHECKED_CAST")
//            if (modelClass.isAssignableFrom(DynamicListViewModel::class.java)) {
//                return DynamicListViewModel(kind) as T
//            } else {
//                throw IllegalArgumentException("ViewModel Not Found.")
//            }
//        }
//    }
}