package com.mredrock.cyxbs.qa.pages.dynamic.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Comment
import com.mredrock.cyxbs.qa.beannew.CommentReleaseResult
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.ReplyInfo
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.utils.removeContinuousEnters
import com.mredrock.cyxbs.api.store.IStoreService

/**
 * @Author: zhangzhe
 * @Date: 2020/11/27 23:52
 */

open class DynamicDetailViewModel : BaseViewModel() {

    val commentList = MutableLiveData<List<Comment>>()

    val loadStatus = MutableLiveData<Int>()

    val replyInfo = SingleLiveEvent<ReplyInfo>()

    val dynamic = MutableLiveData<Dynamic>()

    var position = -1

    val commentReleaseResult = SingleLiveEvent<CommentReleaseResult>()

    val deleteDynamic = MutableLiveData<Boolean>()

    /**
     * bug修复代码
     * @author wx
     * @Date：2021/8/31
     * 限制livedata的粘性事件  造成activity被finish
     */
    var isNeedFinish=false

    // commentId用于刷新后聚焦到某一个评论。
    fun refreshCommentList(postId: String, commentId: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .getPostInfo(postId)
            .mapOrThrowApiException()
            .setSchedulers()
            .doOnSubscribe {
            }
            .doOnError {
            }
            .unsafeSubscribeBy {
                if (it == null) {
                    toast("帖子不存在或已删除")
                }else {
                    ServiceManager(IStoreService::class)
                        .postTask(IStoreService.Task.SEE_DYNAMIC, postId)
                }
                dynamic.postValue(it)
            }

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
            .unsafeSubscribeBy { list ->
                loadStatus.value = NetworkState.SUCCESSFUL
                commentList.postValue(list.reversed())
                position = findCommentByCommentId(list.reversed(), commentId)
            }
    }

    // 回复后，滑动到刚刚回复的comment下
    private fun findCommentByCommentId(dataList: List<Comment>, commentId: String): Int {

        if (commentId == "" || commentId == "-1") {
            return -1
        }
        for (i in dataList.indices) {
            // 如果是直接回复
            if (dataList[i].commentId == commentId) {
                return i
            }
            // 如果回复是回复别人，则滚动到被回复的comment地方
            if (dataList[i].replyList.isNotEmpty()) {
                for (j in dataList[i].replyList.indices) {
                    if (dataList[i].replyList[j].commentId == commentId) {
                        return i
                    }
                }
            }
        }
        return -1
    }

    fun releaseComment(postId: String, content: String) {
        if (content.isBlank()) {
            toastEvent.value = R.string.qa_hint_content_empty
            return
        }
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .releaseComment(
                content.removeContinuousEnters(), postId, replyInfo.value?.replyId
                    ?: ""
            )
            .mapOrThrowApiException()
            .setSchedulers()
            .doOnSubscribe {
                progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT
            }
            .doFinally {
                progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT
            }
            .doOnError {

            }
            .unsafeSubscribeBy {
                commentReleaseResult.postValue(it)
                ServiceManager(IStoreService::class)
                    .postTask(IStoreService.Task.POST_COMMENT, content) // 更新发送评论的任务
            }
    }

    fun deleteDynamic(id: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .deleteDynamic(id)
            .setSchedulers()
            .doOnSubscribe {
                progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT
            }
            .doFinally {
                progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT
            }
            .doOnError {
                toastEvent.value = R.string.qa_delete_dynamic_failure
            }
            .unsafeSubscribeBy {
                isNeedFinish=true
                deleteDynamic.postValue(true)
                toastEvent.value = R.string.qa_delete_dynamic_success
            }
    }

    fun deleteComment(id: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .deleteComment(id)
            .setSchedulers()
            .doOnSubscribe {
                progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT
            }
            .doFinally {
                progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT
            }
            .doOnError {
                toastEvent.value = R.string.qa_delete_comment_failure
            }
            .unsafeSubscribeBy {
                refreshCommentList(dynamic.value?.postId ?: "0", "-1")
                toastEvent.value = R.string.qa_delete_comment_success
            }
    }

    fun reportDynamic(id: String, content: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .reportDynamic(id,content)
            .setSchedulers()
            .doOnSubscribe {
                progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT
            }
            .doFinally {
                progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT
            }
            .doOnError {
                toastEvent.value = R.string.qa_report_dynamic_failure
            }
            .unsafeSubscribeBy {
                if (it.status == 200)
                    toastEvent.value = R.string.qa_report_dynamic_success
            }
    }

    fun reportComment(id: String, content: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .reportComment(id,content)
            .setSchedulers()
            .doOnSubscribe {
                progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT
            }
            .doFinally {
                progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT
            }
            .doOnError {
                toastEvent.value = R.string.qa_report_comment_failure
            }
            .unsafeSubscribeBy {
                if (it.status == 200)
                    toastEvent.value = R.string.qa_report_comment_success
            }
    }

    class Factory : ViewModelProvider.Factory {

        companion object {
            var viewModel: ViewModel? = null
        }

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")

            if (modelClass.isAssignableFrom(DynamicDetailViewModel::class.java)) {

                return if (viewModel == null) {
                    DynamicDetailViewModel().also {
                        viewModel = it
                    } as T
                } else {
                    viewModel as T
                }
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }


}