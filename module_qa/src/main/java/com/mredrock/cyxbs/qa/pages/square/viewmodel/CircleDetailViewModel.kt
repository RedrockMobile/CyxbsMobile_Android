package com.mredrock.cyxbs.qa.pages.square.viewmodel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.pages.square.model.CircleDynamicDataSource

/**
 *@Date 2020-11-23
 *@Time 21:10
 *@author SpreadWater
 *@description
 */
open class CircleDetailViewModel(kind: String, loop: Int) : BaseViewModel() {

    val circleDynamicList: LiveData<PagedList<Dynamic>>
    val networkState: LiveData<Int>
    val initialLoad: LiveData<Int>
    private val factory: CircleDynamicDataSource.Factory

    val ignorePeople = MutableLiveData<Boolean>()
    val deleteTips = MutableLiveData<Boolean>()
    var topicId = 1
    //Activity观察的liveData，用于监测内部Fragment引起的FollowState的改变
    val followStateChangedMarkObservableByActivity = MutableLiveData<Boolean>()
    //Fragment观察的liveData，用于监测内部Activity引起的FollowState的改变
    val followStateChangedMarkObservableByFragment = MutableLiveData<Boolean>()
    init {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(3)
            .setPageSize(6)
            .setInitialLoadSizeHint(6)
            .build()
        factory = CircleDynamicDataSource.Factory(kind, loop)
        circleDynamicList = LivePagedListBuilder<Int, Dynamic>(factory, config).build()
        networkState =
            Transformations.switchMap(factory.circleDynamicDataSourceLiveData) { it.networkState }
        initialLoad =
            Transformations.switchMap(factory.circleDynamicDataSourceLiveData) { it.initialLoad }

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
            }
            .safeSubscribeBy {
                deleteTips.value = true
                toastEvent.value = R.string.qa_delete_dynamic_success
            }
    }

    fun followTopic(topicName: String, followState: Boolean) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .followTopicGround(topicName)
            .setSchedulers()
            .safeSubscribeBy {
                if (it.status == 200) {
                    if (followState) {
                        //如果处于关注状态,点击之后是取消关注
                        toastEvent.value = R.string.qa_unfollow_circle
                    } else {
                        //如果处于未关注状态,点击之后是关注
                        toastEvent.value = R.string.qa_follow_circle
                    }
                }
            }
    }

    fun invalidateQuestionList() = circleDynamicList.value?.dataSource?.invalidate()

    fun retry() = factory.circleDynamicDataSourceLiveData.value?.retry()

    class Factory(private val kind: String, private val loop: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(CircleDetailViewModel::class.java)) {
                return CircleDetailViewModel(kind, loop) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}