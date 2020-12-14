package com.mredrock.cyxbs.qa.pages.dynamic.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.beannew.Comment
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.network.NetworkState

/**
 * @Author: sandyz987
 * @Date: 2020/11/27 23:52
 */

open class DynamicDetailViewModel : BaseViewModel() {

    val commentList = MutableLiveData<List<Comment>>()

    val loadStatus = MutableLiveData<Int>()


    fun getCommentList(postId: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getComment(postId)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnSubscribe {
                    loadStatus.postValue(NetworkState.LOADING)
                }
                .doOnError {
                    loadStatus.value = NetworkState.FAILED
                }
                .safeSubscribeBy { list ->
                    loadStatus.value = NetworkState.SUCCESSFUL
                    commentList.value = list
                }
    }


    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(DynamicDetailViewModel::class.java)) {
                return DynamicDetailViewModel() as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }


}