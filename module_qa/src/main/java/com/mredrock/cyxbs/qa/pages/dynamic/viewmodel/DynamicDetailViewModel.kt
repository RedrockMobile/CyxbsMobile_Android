package com.mredrock.cyxbs.qa.pages.dynamic.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.beannew.CommentOuter
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.network.NetworkState

/**
 * @Author: sandyz987
 * @Date: 2020/11/27 23:52
 */

open class DynamicDetailViewModel : BaseViewModel() {

    val commentList = MutableLiveData<List<CommentOuter>>()

    val loadStatus = MutableLiveData<Int>()


    fun getCommentList(postId: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getComment(postId)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnSubscribe {
                    loadStatus.postValue(NetworkState.LOADING)

                    Log.e("sandyzhang", "loading")
                }
                .doOnError {
                    loadStatus.value = NetworkState.FAILED
                    Log.e("sandyzhang", it.toString())
                }
                .safeSubscribeBy { list ->
                    loadStatus.value = NetworkState.SUCCESSFUL
                    commentList.value = list
                    Log.e("sandyzhang", list.toString())
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