package com.mredrock.cyxbs.mine.page.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.mine.network.model.Comment
import com.mredrock.cyxbs.mine.network.model.CommentReceived
import com.mredrock.cyxbs.mine.network.model.NavigateData
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.disposeAll
import com.mredrock.cyxbs.mine.util.extension.normalWrapper
import com.mredrock.cyxbs.mine.util.widget.RvFooter
import io.reactivex.disposables.Disposable

/**
 * Created by zia on 2018/9/13.
 */
class CommentViewModel : BaseViewModel() {
    private val pageSize = 6
    private var commentPage: Int = 1
    private var commentReceivedPage: Int = 1

    private val disposableForComment: MutableList<Disposable> = mutableListOf()
    private val disposableForReComment: MutableList<Disposable> = mutableListOf()

    //发出评论部分
    private val _eventOnComment = SingleLiveEvent<RvFooter.State>()
    val eventOnComment: LiveData<RvFooter.State>
        get() = _eventOnComment

    private val _commentList = MutableLiveData<MutableList<Comment>>()
    val commentList: LiveData<List<Comment>>
        get() = Transformations.map(_commentList) {
            it.toList()
        }

    fun loadCommentList() {
        val disposable = apiService.getCommentList(commentPage++, pageSize)
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            //当为空的说明这一页没有数据，于是停止加载
                            if (it.isEmpty()) {
                                //由于异步的原因，可能会出现后面的page先返回的情况，会把State设置为NOTHING，但是实际上列表并不为空，暂时没有好的解决办法
                                if (_commentList.value.isNullOrEmpty()) {
                                    _eventOnComment.postValue(RvFooter.State.NOTHING)
                                    return@safeSubscribeBy
                                } else {
                                    _eventOnComment.postValue(RvFooter.State.NOMORE)
                                    return@safeSubscribeBy
                                }
                            }

                            val localComment = _commentList.value ?: mutableListOf()
                            localComment.addAll(it)
                            _commentList.postValue(localComment)
                            //下一页
                            loadCommentList()
                        },
                        onError = {
                            _eventOnComment.postValue(RvFooter.State.ERROR)
                        })
                .lifeCycle()
        disposableForComment.add(disposable)
    }

    fun cleanCommentPage() {
        //清除还在请求网络的接口,
        //如果一直刷新，那么前一个网络请求没有cancel掉，那么就会导致多的item
        disposeAll(disposableForComment)

        commentPage = 1
        _commentList.value = mutableListOf()
    }


    //收到评论部分
    private val _eventOnCommentReceived = SingleLiveEvent<RvFooter.State>()
    val eventOnCommentReceived: LiveData<RvFooter.State>
        get() = _eventOnCommentReceived

    private val _commentReceivedList = MutableLiveData<MutableList<CommentReceived>>()
    val commentReceivedList: LiveData<List<CommentReceived>>
        get() = Transformations.map(_commentReceivedList) {
            it.toList()
        }

    fun loadCommentReceivedList() {
        val disposable = apiService.getCommentReceivedList(commentReceivedPage++, pageSize)
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            //当为空的说明这一页没有数据，于是停止加载
                            if (it.isEmpty()) {
                                if (_commentReceivedList.value.isNullOrEmpty()) {
                                    _eventOnCommentReceived.postValue(RvFooter.State.NOTHING)
                                    return@safeSubscribeBy
                                } else {
                                    _eventOnCommentReceived.postValue(RvFooter.State.NOMORE)
                                    return@safeSubscribeBy
                                }
                            }

                            val localCommentReceived = _commentReceivedList.value ?: mutableListOf()
                            localCommentReceived.addAll(it)
                            _commentReceivedList.postValue(localCommentReceived)
                            //下一页
                            loadCommentReceivedList()
                        },
                        onError = {
                            _eventOnCommentReceived.postValue(RvFooter.State.ERROR)
                        })
                .lifeCycle()
        disposableForReComment.add(disposable)
    }

    fun cleanCommentReceivedPage() {
        //清除还在请求网络的接口,
        //如果一直刷新，那么前一个网络请求没有cancel掉，那么就会导致多的item
        disposeAll(disposableForReComment)

        commentReceivedPage = 1
        _commentReceivedList.value = mutableListOf()
    }


    //发出的评论跳转监听
    val navigateEventOnComment = SingleLiveEvent<NavigateData>()
    //收到的评论跳转监听
    val navigateEventOnReComment = SingleLiveEvent<NavigateData>()

    fun getAnswerFromComment(qid: Int, id: Int) {
        apiService.getAnswer(id.toString())
                .setSchedulers()
                .safeSubscribeBy (
                        onNext = {
                            val navigateData = NavigateData(qid, id, it.string())
                            navigateEventOnComment.postValue(navigateData)
                        },
                        onError = {
                            BaseApp.context.toast("获取评论信息失败")
                        }
                )
                .lifeCycle()
    }

    fun getAnswerFromReComment(qid: Int, id: Int) {
        apiService.getAnswer(id.toString())
                .setSchedulers()
                .safeSubscribeBy (
                        onNext = {
                            val navigateData = NavigateData(qid, id, it.string())
                            navigateEventOnReComment.postValue(navigateData)
                        },
                        onError = {
                            BaseApp.context.toast("获取评论信息失败")
                        }
                )
                .lifeCycle()
    }
}